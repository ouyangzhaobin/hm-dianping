package com.hmdp;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author itzyh
 * @since 2023-08-11 22:22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 开启虚拟mvc调用
@AutoConfigureMockMvc
public class TokenTest {
    @Autowired
    private MockMvc mockMvc;

    @Resource
    private IUserService iUserService;

    public static List<String> tokenList = new ArrayList<>();

    @Test
    void token() throws Exception {
        List<String> phones = iUserService.listObjs(Wrappers.<User>lambdaQuery().select(User::getPhone)
                .last("limit 0, 1000"), Object::toString);
        phones.forEach(phone -> {
            try {
                String response = mockMvc.perform(MockMvcRequestBuilders.post("/user/code")
                                .queryParam("phone", phone))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn().getResponse().getContentAsString();
                Result result = JSONUtil.toBean(response, Result.class);
                Assert.isTrue(result.getSuccess(), String.format("获取“%s”手机号的验证码失败", phone));

                // String code = result.getData().toString();
                String code = "123456";
                LoginFormDTO loginFormDTO = LoginFormDTO.builder().code(code).phone(phone).build();
                String requestBody = JSONUtil.toJsonStr(loginFormDTO);
                System.out.println(requestBody);
                String response2 = mockMvc.perform(MockMvcRequestBuilders
                                .post("/user/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn().getResponse().getContentAsString();

                result = JSONUtil.toBean(response2, Result.class);
                String token = result.getData().toString();
                tokenList.add(token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        writeToken();
    }

    private static void writeToken() {
        // 文件路径
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources/";

        try {
            // 创建文件输出流
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "token.txt");

            // 创建 PrintStream，将输出重定向到文件
            PrintStream printStream = new PrintStream(fileOutputStream);

            // 输出内容到文件
            tokenList.forEach(s -> {
                printStream.println(s);
                printStream.flush();
            });

            // 关闭文件输出流
            printStream.close();
        } catch (IOException ignored) {
        }
    }
}
