package com.byte102.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by useheart on 2023/7/11
 * @author useheart
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnableRequestJsonApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EnableRequestJsonTest {

	@Autowired
	private MockMvc mockMvc;

	public static class TestA {
		public String a = "testA";

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			TestA testA = (TestA) o;
			return Objects.equals(a, testA.a);
		}

		@Override
		public int hashCode() {
			return Objects.hash(a);
		}

		@Override
		public String toString() {
			return "TestA{" +
					"a='" + a + '\'' +
					'}';
		}
	}

	public static class TestB {
		public String b = "testB";

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			TestB testB = (TestB) o;
			return Objects.equals(b, testB.b);
		}

		@Override
		public int hashCode() {
			return Objects.hash(b);
		}

		@Override
		public String toString() {
			return "TestB{" +
					"b='" + b + '\'' +
					'}';
		}
	}

	public static class TestJson {
		public boolean bool = true;
		public int int1 = 11;
		public Long long1 = 1111L;
		public String str = "hello world";
		public Object object = new Object();
		public TestA testA = new TestA();
		public TestB testB = new TestB();
		public List<TestA> testAList = Arrays.asList(new TestA(), new TestA());
		public Set<Object> objectSet = new HashSet<>(Arrays.asList(new TestA(), new TestB()));
		public Map<String, TestB> testMap = Collections.singletonMap("testB", new TestB());
	}

    @Test
    public void bodyJson() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        final String content = objectMapper.writeValueAsString(new TestJson());
        System.out.println("requestBefore" + content);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("success")));
    }

	@Test
	public void emptyChar() throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		final String content = objectMapper.writeValueAsString(new TestJson());
		System.out.println("requestBefore" + content);
		this.mockMvc.perform(MockMvcRequestBuilders.post("/test")
						.contentType(MediaType.APPLICATION_JSON)
						.content(""))
				.andDo(print())
                .andExpect(result -> AssertionErrors.assertEquals("Resolved Exception", MissingServletRequestParameterException.class, result.getResolvedException().getClass()));
	}

    @Test
    public void null1() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        final String content = objectMapper.writeValueAsString(new TestJson());
        System.out.println("requestBefore" + content);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> AssertionErrors.assertEquals("Resolved Exception", MissingServletRequestParameterException.class, result.getResolvedException().getClass()));
    }

    @Test
    public void emptyObject() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        final String content = objectMapper.writeValueAsString(new TestJson());
        System.out.println("requestBefore" + content);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(result -> AssertionErrors.assertEquals("Resolved Exception", MissingServletRequestParameterException.class, result.getResolvedException().getClass()));
    }
}
