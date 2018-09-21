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
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.persistence.Id;
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
		if(annotations.size() <= 0 || roundEnvironment.processingOver())
			return false;
		for(Element k : roundEnvironment.getRootElements()) {
			if(!isEntity(k))
				continue;
			// it's an entity, we good fam
			buildClass(k);
		}//*/
		return false;
	}

	static boolean wantsDao(Element k) {
		if(k.getAnnotationsByType(GenerateDAO.class).length > 0)
			return true;
		return false;
	}

	static boolean isEntity(Element k) {
		if(k.getAnnotationsByType(Entity.class).length < 1)
			return false;
		for(Element f : k.getEnclosedElements()) {
			if(f.getAnnotationsByType(Id.class).length > 0)
				return true;
		}
		return false;
	}

	private void buildClass(Element k) {
		String daoName = k.getSimpleName() + DAO_NAME_SUFFIX;
		String fullyQualifiedDaoName = String.join(".", getPackageDeclaration(k), daoName);
		
		TypeSpec finder = FindBuilderGenerator.generateBuilder(pe, k); // always generate a builder

		if(!wantsDao(k)) // if it doesn't want a DAO, okay
			return;

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

		TypeSpec daoSpec = TypeSpec.classBuilder(daoName)
			.superclass(ParameterizedTypeName.get(ClassName.get(GenericPersistenceManager.class), entityKlasse, primaryKeyKlasse))
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
			JavaFile javaFile = JavaFile.builder(getPackageDeclaration(k), daoSpec).indent("	").build();
			javaFile.writeTo(w);
			w.flush();
			w.close();
		} catch(IOException ex) {
			//
		}
	}

	static String getPackageDeclaration(Element k) {
		String[] tokens = k.toString().split("\\.");
		String output = tokens[0];
		for(int i = 1; i < (tokens.length-1); ++i){
			output = String.join(".", output, tokens[i]);
		}
		return output;
	}

	static String getPackageOf(String fqtn) {
		String[] tokens = fqtn.split("\\.");
		String output = tokens[0];
		for(int i = 1; i < (tokens.length-1); ++i){
			output = String.join(".", output, tokens[i]);
		}
		return output;
	}

	static String getSimpleNameOf(String fqtn) {
		String[] tokens = fqtn.split("\\.");
		return tokens[tokens.length-1];
	}

	static String getPrimaryKeyType(Element k) {
		for(Element f : k.getEnclosedElements()) {
			if(f.getAnnotationsByType(Id.class).length < 1) {
				continue;
			}
			return f.asType().toString();
		}
		return "";
	}

	static String filterfqtn(String fqtn) {
		if(fqtn.startsWith("java.lang."))
			return fqtn.replace("java.lang.", "");
		return fqtn;
	}
}
