package com.secondhand.web.controller.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ReUtil;
import com.secondhand.common.core.domain.R;
import com.secondhand.common.exception.base.BaseException;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.common.utils.email.EmailVerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.code.kaptcha.Producer;
import com.secondhand.common.config.RuoYiConfig;
import com.secondhand.common.constant.CacheConstants;
import com.secondhand.common.constant.Constants;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.redis.RedisCache;
import com.secondhand.common.utils.sign.Base64;
import com.secondhand.common.utils.uuid.IdUtils;
import com.secondhand.system.service.ISysConfigService;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@RestController
public class CaptchaController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private EmailVerificationUtil emailVerificationUtil;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) throws IOException
    {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled)
        {
            return ajax;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        String captchaType = RuoYiConfig.getCaptchaType();
        if ("math".equals(captchaType))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(captchaType))
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /** 生成邮箱验证码*/
    @PostMapping("/emailCode")
    public R<String> sendMessageToEmail(@RequestParam("email") String email) {

        if (StringUtils.isAnyBlank(email)) {
            throw new BaseException("当前输入邮箱数据为空");
        }

        // 校验邮箱
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // 更宽松的邮箱校验正则表达式
        if(!ReUtil.isMatch(regex, email)){
            throw new BaseException("邮箱格式错误");
        }

        // 获取当前邮箱在redis存储的键值
        String verifyKey = CacheConstants.EMAIL_VERIFICATION_CODE_KEY + email;

        // 从redis中查看有没有该邮箱的验证码
        String  verifyCode = redisCache.getCacheObject(verifyKey);
        if (!StringUtils.isAnyBlank(verifyCode)) {
            throw new BaseException("验证码已经发送，请稍后重试");
        }

        // 预存储键值，防止邮箱发送过慢，再次点击发送邮箱导致多次发送邮箱验证码
        redisCache.setCacheObject(verifyKey, "empty", Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 如果redis没有该邮箱验证码，则获取验证码并发送短信
        verifyCode = emailVerificationUtil.sendVerificationCodeToEmail(email);
        // 将该验证码存入redis
        redisCache.setCacheObject(verifyKey, verifyCode, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        return R.ok("发送成功");
    }
}
