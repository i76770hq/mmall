package pub.zjh.mall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pub.zjh.mall.dao.IBaseBatchOperation;

import java.util.Collection;

@Service
@Slf4j
public class BatchOperationService {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 批量更新
     */
    public <T> void batchUpdate(Collection<T> collection, Class<? extends IBaseBatchOperation<T>> mapperClass) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            IBaseBatchOperation mapper = sqlSession.getMapper(mapperClass);
            //提交数量，到达这个数量就提交
            int batchCount = 1000;
            int i = 0;
            for (T t : collection) {
                mapper.updateByPrimaryKeySelective(t);
                if (i != 0 && i % batchCount == 0) {
                    sqlSession.commit();
                }
                i++;
            }
            if (i != batchCount) {
                sqlSession.commit();
            }
        } catch (Exception e) {
            sqlSession.rollback();
            log.error("{}", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }

    }

}
