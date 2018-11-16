package com.jet.compiler;

import com.com.jet.protocol.ProtocolBean;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.jet.annotation.Implement;
import com.jet.annotation.Interface;

import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * @author zhengxiaobin
 * @since 17/7/27
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ProtocolProcessor extends AbstractProcessor {


    public static final String ASSET_PATH = "assets/protocol/";
    public static final String FILE_SUFFIX = ".json";

    /**
     * APT 默认目录
     */
    Filer filer;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        try {
            //APT 会执行多轮，需要过滤掉
            if (annotations == null || annotations.isEmpty()) {
                System.out.println(">>> annotations is null... <<<");
                return true;
            }
            ArrayList<ProtocolBean> list = new ArrayList<>();

            for (TypeElement annotation : annotations) {
                Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    ProtocolBean bean = new ProtocolBean();
                    TypeElement typeElement = (TypeElement) element;
                    Interface anInterface = typeElement.getAnnotation(Interface.class);
                    if (anInterface != null) {
                        bean.key = anInterface.value();
                        bean.key_interface = typeElement.getQualifiedName().toString();
                    }
                    Implement anImplement = typeElement.getAnnotation(Implement.class);
                    if (anImplement != null) {
                        bean.key = anImplement.value();
                        bean.key_implement = typeElement.getQualifiedName().toString();
                    }
                    list.add(bean);

                }
            }

            String content = new Gson().toJson(list);
            System.out.println(content);
            int hashCode = types.hashCode();
            String path = ASSET_PATH + hashCode + FILE_SUFFIX;

            FileObject fileObject = filer
                    .createResource(StandardLocation.CLASS_OUTPUT, "", path);
            Writer writer = fileObject.openWriter();
            writer.write(content);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(Interface.class.getCanonicalName());
        annotations.add(Implement.class.getCanonicalName());

        return annotations;
    }
}
