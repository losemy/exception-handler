

### 使用方式
1. 添加注解@EnableBizExceptionHandler
2. 在需要进行拦截的类上添加@Biz注解
3. exception匹配遵循就近原则，因此需要一个@BizHandler(value = Exception.class)兜底
4. 需要尽量保证相同异常的返回值是一致的（最好是统一返回值）
