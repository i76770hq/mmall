package pub.zjh.mall.shiro;

import com.google.common.collect.Sets;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.util.RedisPoolUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RedisSessionDao extends AbstractSessionDAO {

    public static final String REDIS_SESSION_KEY = "shiro-session:";

    @Autowired
    private RedisPoolUtil redisPoolUtil;

    private byte[] getKey(String sessionId) {
        return (REDIS_SESSION_KEY + sessionId).getBytes();
    }

    private void saveSession(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        byte[] key = getKey(session.getId().toString());
        byte[] value = SerializationUtils.serialize(session);
        redisPoolUtil.set(key, value);
        redisPoolUtil.expire(key, MallConst.REDIS_SESSION_EXPIRE);
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = redisPoolUtil.get(key);
        return (Session) SerializationUtils.deserialize(value);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        byte[] key = getKey(session.getId().toString());
        redisPoolUtil.del(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = redisPoolUtil.keys((REDIS_SESSION_KEY + "*").getBytes());
        if (CollectionUtils.isEmpty(keys)) {
            return Sets.newHashSet();
        }
        Set<Session> sessionSet = keys.stream().
                map(bytes -> (Session) SerializationUtils.deserialize(bytes)).
                collect(Collectors.toSet());
        return sessionSet;
    }

}
