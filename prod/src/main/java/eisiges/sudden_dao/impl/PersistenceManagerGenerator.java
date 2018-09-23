package eisiges.sudden_dao.impl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import eisiges.sudden_dao.GenerateDAO;
import eisiges.sudden_dao.GenericPersistenceManager;
import static eisiges.sudden_dao.impl.AnnotationProcessingUtils.*;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

/**
 * @author kg6zvp
 */
@SupportedAnnotationTypes({
	"eisiges.sudden_dao.GenerateDAO",
	"javax.persistence.Entity"
})
@SupportedOptions({})
@AutoService(Processor.class)
public class PersistenceManagerGenerator extends AbstractProcessor {

	public static final String DAO_NAME_SUFFIX = "DAO";

	ProcessingEnvironment pe;

	@Override
	public synchronized void init(ProcessingEnvironment pe) {
		super.init(pe);
		this.pe = pe;
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {
		if (annotations.size() <= 0 || roundEnvironment.processingOver()) {
			return false;
		}
		for (Element k : roundEnvironment.getRootElements()) {
			if (!isEntity(k)) {
				continue;
			}
			// it's an entity, we good fam
			buildClass(k);
		}
		return false;
	}

	private void buildClass(Element k) {
		String daoName = k.getSimpleName() + DAO_NAME_SUFFIX;
		String fullyQualifiedDaoName = String.join(".", getPackageDeclaration(k), daoName);

		TypeSpec finder = FindBuilderGenerator.generateBuilder(pe, k); // always generate a builder

		if (!wantsDao(k)) { // if it doesn't want a DAO, okay
			return;
		}

		ClassName entityKlasse = ClassName.get(getPackageDeclaration(k), k.getSimpleName().toString());
		ClassName primaryKeyKlasse = ClassName.get(getPackageOf(getPrimaryKeyType(k)), getSimpleNameOf(getPrimaryKeyType(k)));

		MethodSpec constructor = MethodSpec.constructorBuilder()
		    .addModifiers(Modifier.PUBLIC)
		    .addStatement("super($T.class)", entityKlasse)
		    .build();

		ClassName finderCN = ClassName.get(getPackageDeclaration(k), finder.name);

		MethodSpec findMethod = MethodSpec.methodBuilder("find")
		    .addModifiers(Modifier.PUBLIC)
		    .returns(finderCN)
		    .addStatement("return new $T(em)", finderCN)
		    .build();

		AnnotationMirror daoAnnotation = getAnnotation(pe.getElementUtils().getAllAnnotationMirrors(k), GenerateDAO.class);
		AnnotationValue customParentClass = getAnnotationValue(daoAnnotation, "parentClass");
		ClassName daoParent = customParentClass == null ? ClassName.get(GenericPersistenceManager.class) : getClassNameFromFqtn(customParentClass.getValue().toString());
		
		TypeSpec daoSpec = TypeSpec.classBuilder(daoName)
		    .superclass(ParameterizedTypeName.get(daoParent, entityKlasse, primaryKeyKlasse))
		    .addModifiers(Modifier.PUBLIC)
		    .addAnnotation(
			AnnotationSpec
			    .builder(Generated.class)
			    .addMember("value", "{\"" + PersistenceManagerGenerator.class.getName() + "\"}")
			    .build())
		    .addMethod(constructor)
		    .addMethod(findMethod)
		    .build();

		try {
			FileObject fileObject = pe.getFiler().createSourceFile(fullyQualifiedDaoName);
			Writer w = fileObject.openWriter();
			JavaFile javaFile = JavaFile.builder(getPackageDeclaration(k), daoSpec).indent("		").build();
			javaFile.writeTo(w);
			w.flush();
			w.close();
		} catch (IOException ex) {
		}
	}
}
