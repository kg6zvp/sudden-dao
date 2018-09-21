package eisiges.sudden_dao.impl;

import eisiges.sudden_dao.GenerateDAO;
import javax.lang.model.element.Element;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author kg6zvp
 */
class AnnotationProcessingUtils {

	static boolean wantsDao(Element k) {
		if (k.getAnnotationsByType(GenerateDAO.class).length > 0) {
			return true;
		}
		return false;
	}

	static boolean isEntity(Element k) {
		if (k.getAnnotationsByType(Entity.class).length < 1) {
			return false;
		}
		for (Element f : k.getEnclosedElements()) {
			if (f.getAnnotationsByType(Id.class).length > 0) {
				return true;
			}
		}
		return false;
	}

	static String getSimpleNameOf(String fqtn) {
		String[] tokens = fqtn.split("\\.");
		return tokens[tokens.length - 1];
	}

	static String getPrimaryKeyType(Element k) {
		for (Element f : k.getEnclosedElements()) {
			if (f.getAnnotationsByType(Id.class).length < 1) {
				continue;
			}
			return f.asType().toString();
		}
		return "";
	}

	static String getPackageDeclaration(Element k) {
		String[] tokens = k.toString().split("\\.");
		String output = tokens[0];
		for (int i = 1; i < (tokens.length - 1); ++i) {
			output = String.join(".", output, tokens[i]);
		}
		return output;
	}

	static String filterfqtn(String fqtn) {
		if (fqtn.startsWith("java.lang.")) {
			return fqtn.replace("java.lang.", "");
		}
		return fqtn;
	}

	static String getPackageOf(String fqtn) {
		String[] tokens = fqtn.split("\\.");
		String output = tokens[0];
		for (int i = 1; i < (tokens.length - 1); ++i) {
			output = String.join(".", output, tokens[i]);
		}
		return output;
	}
}
