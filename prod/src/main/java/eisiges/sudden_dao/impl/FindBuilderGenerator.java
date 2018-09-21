package eisiges.sudden_dao.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import eisiges.sudden_dao.FindBuilder;
import javax.lang.model.element.Element;

import static eisiges.sudden_dao.impl.AnnotationProcessingUtils.*;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.persistence.EntityManager;
import javax.tools.FileObject;

/**
 * @author kg6zvp
 */
public class FindBuilderGenerator {

	public static TypeSpec generateBuilder(ProcessingEnvironment pe, Element k) {
		String finderName = k.getSimpleName() + "QueryBuilder";
		String packageName = getPackageDeclaration(k);
		String fullyQualifiedClassName = String.join(".", packageName, finderName);

		ClassName entityKlasse = ClassName.get(getPackageDeclaration(k), k.getSimpleName().toString());

		MethodSpec constructor = MethodSpec.constructorBuilder()
		    .addModifiers(Modifier.PUBLIC)
		    .addParameter(EntityManager.class, "em")
		    .addStatement("super($T.class, em)", entityKlasse)
		    .build();

		ClassName findBuilderCN = ClassName.get(FindBuilder.class);

		TypeSpec finder = TypeSpec.classBuilder(finderName)
		    .superclass(ParameterizedTypeName.get(findBuilderCN, entityKlasse))
		    .addModifiers(Modifier.PUBLIC)
		    .addAnnotation(
			AnnotationSpec
			    .builder(Generated.class)
			    .addMember("value", "{\"" + FindBuilderGenerator.class.getName() + "\"}")
			    .build())
		    .addMethod(constructor)
		    .build();

		JavaFile javaFile = JavaFile
		    .builder(packageName, finder)
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

		return finder;
	}
}
