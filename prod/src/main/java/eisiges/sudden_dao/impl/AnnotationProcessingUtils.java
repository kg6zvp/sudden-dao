package eisiges.sudden_dao.impl;

import eisiges.sudden_dao.GenerateDAO;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.tools.FileObject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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

	static TypeName getFieldType(Element field) {
		return ClassName.get(field.asType());
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

	static AnnotationMirror getAnnotation(List<? extends AnnotationMirror> li, Class<? extends Annotation> anno) {
		for(AnnotationMirror am : li) {
			if(anno.getName().equals(am.getAnnotationType().toString())) {
				return am;
			}
		}
		return null;
	}

	static AnnotationValue getAnnotationValue(AnnotationMirror am, String key) {
		for(Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
			if(key.equals(entry.getKey().getSimpleName().toString())) {
				return entry.getValue();
			}
		}
		return null;
	}

	static ClassName getClassNameFromFqtn(String fqtn) {
		return ClassName.get(getPackageOf(fqtn), getSimpleNameOf(fqtn));
	}

	static TypeSpec writeJavaFile(ProcessingEnvironment pe, String packageName, TypeSpec type) {
		String fullyQualifiedClassName = String.join(".", packageName, type.name);

		JavaFile javaFile = JavaFile
		    .builder(packageName, type)
		    .skipJavaLangImports(true)
		    .indent("	")
		    .build();
		try {
			FileObject fo = pe.getFiler().createSourceFile(fullyQualifiedClassName);
			Writer w = fo.openWriter();
			javaFile.writeTo(w);
			w.flush();
			w.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return type;
	}
	
	static <T> AnnotationSpec getGeneratedAnnotation(Class<T> generator) {
		return AnnotationSpec
			.builder(Generated.class)
			.addMember("value", "{\"" + generator.getName() + "\"}")
			.build();
	}
}
