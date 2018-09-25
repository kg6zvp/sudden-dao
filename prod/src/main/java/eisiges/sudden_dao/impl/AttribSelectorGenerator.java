package eisiges.sudden_dao.impl;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import static eisiges.sudden_dao.impl.AnnotationProcessingUtils.*;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * @author kg6zvp
 */
public class AttribSelectorGenerator {
	public static final String ATTR_SELECTOR_SUFFIX = "AttribSelector";
	public static final TypeVariableName TYPE_PARAM = TypeVariableName.get("T");

	ProcessingEnvironment pe;

	public AttribSelectorGenerator(ProcessingEnvironment pe) {
		this.pe = pe;
	}

	public TypeSpec generateInterface(Element k) {
		List<MethodSpec> selectorMethods = new LinkedList<>();

		for (Element f : k.getEnclosedElements()) {
			if(f.getKind().equals(ElementKind.FIELD))
				selectorMethods.add(buildSelector(k, f));
		}
		TypeSpec attribInterface = TypeSpec.interfaceBuilder(k.getSimpleName() + ATTR_SELECTOR_SUFFIX)
			.addTypeVariable(TYPE_PARAM)
			.addModifiers(Modifier.PUBLIC)
			.addJavadoc("Selects attributes from {@link $T}\n", getFieldType(k))
			.addAnnotation(getGeneratedAnnotation(AttribSelectorGenerator.class))
			.addMethods(selectorMethods)
			.build();

		return writeJavaFile(pe, getPackageDeclaration(k), attribInterface);
	}

	MethodSpec buildSelector(Element parent, Element field) {
		return MethodSpec.methodBuilder(field.toString())
			.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			.addJavadoc("Selects {@link $T#" + field.toString() + "}\n"
			          + "@returns T\n", getFieldType(parent))
			.returns(TYPE_PARAM)
			.build();
	}
}