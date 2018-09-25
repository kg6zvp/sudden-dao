package eisiges.generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class AttrSelectorGeneratorTest {
	@Test
	public void testShouldGenerateAttrSelector() { // test will fail if this doesn't compile, which is all we need
		UserModelAttribSelector<String> selector = new UserModelAttribSelector<String>(){
			@Override
			public String username() {
				return "username";
			}
		
			@Override
			public String id() {
				return "id";
			}
		
			@Override
			public String fullName() {
				return "fullName";
			}
		
			@Override
			public String birthDate() {
				return "birthDate";
			}
		};

		assertEquals("username", selector.username());
		assertEquals("id", selector.id());
		assertEquals("fullName", selector.fullName());
		assertEquals("birthDate", selector.birthDate());
	}
}