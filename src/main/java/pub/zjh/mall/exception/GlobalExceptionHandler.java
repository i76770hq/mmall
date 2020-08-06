package pub.zjh.mall.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.vo.ResponseVo;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MallException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo mallExceptionHandle(MallException e) {
        log.error("{}", e);
        return ResponseVo.error(ResponseEnum.ERROR, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo mallExceptionHandle(AuthenticationException e) {
        log.error("{}", e);
        return ResponseVo.error(ResponseEnum.ERROR, ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
    }

    @ExceptionHandler({UserLoginException.class, UnauthenticatedException.class})
    public ResponseVo userLoginExceptionHandle() {
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }


    @ExceptionHandler(UnauthorizedException.class)
    //    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo mallExceptionHandle(UnauthorizedException e) {
        log.error("{}", e);
        return ResponseVo.error(ResponseEnum.ERROR, ResponseEnum.NOT_ADMIN_LOGIN_ERROR.getDesc());
    }

    /**
     * 处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVo notValidExceptionHandle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        log.error("{}", e);
        return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
    }

    /**
     * 处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseVo handleConstraintViolationException(ConstraintViolationException e) {
        log.error("{}", e);
        final String message = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getPropertyPath() + ":" + constraintViolation.getMessage())
                .collect(Collectors.joining());
        return ResponseVo.error(ResponseEnum.PARAM_ERROR, message);
    }

    /**
     * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常，详情继续往下看代码
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseVo BindExceptionHandler(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        log.error("{}", e);
        return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
    }

    @ExceptionHandler(Exception.class)
    public ResponseVo exceptionHandle(Exception e) {
        log.error("{}", e);
        return ResponseVo.error(ResponseEnum.ERROR);
    }


}
