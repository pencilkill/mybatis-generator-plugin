/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import com.nd.mybatis.generic.dao.GenericCriteria;
import com.nd.mybatis.generic.dao.GenericMapper;
import com.nd.mybatis.generic.service.GenericService;
import com.nd.mybatis.generic.service.GenericServiceImpl;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class GenericPlugin extends PluginAdapter
{
    private String servicePackage;

    private String serviceImplPackage;

    private IntrospectedTable introspectedTable;

    /**
     * @param warnings
     * @return
     * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
     */
    @Override
    public boolean validate(List<String> warnings)
    {
        servicePackage = properties.getProperty("servicePackage");

        if (!StringUtility.stringHasValue(servicePackage))
        {
            warnings.add("ServicePackage must not be empty");

            return false;
        }

        serviceImplPackage = properties.getProperty("serviceImplPackage");

        if (!StringUtility.stringHasValue(serviceImplPackage))
        {
            warnings.add("ServiceImplPackage must not be empty");

            return false;
        }

        return true;
    }

    /**
     *
     */
    private GeneratedJavaFile generateServiceJavaFile()
    {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        
        FullyQualifiedJavaType qualifiedExampleClass = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        
        FullyQualifiedJavaType qualifiedRootClass = new FullyQualifiedJavaType(GenericService.class.getSimpleName());

        qualifiedRootClass.addTypeArgument(new FullyQualifiedJavaType(qualifiedExampleClass.getShortName()));
        qualifiedRootClass.addTypeArgument(new FullyQualifiedJavaType(domainObjectName));

        String serviceName = servicePackage + "." + domainObjectName + "Service";

        Interface service = new Interface(new FullyQualifiedJavaType(serviceName));

        service.addImportedType(new FullyQualifiedJavaType(GenericService.class.getCanonicalName()));
        service.addImportedType(qualifiedExampleClass);
        service.addImportedType(new FullyQualifiedJavaType(context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + domainObjectName));

        service.setVisibility(JavaVisibility.PUBLIC);
        service.addSuperInterface(qualifiedRootClass);
        
        return new GeneratedJavaFile(service, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
    }

    /**
     * @param introspectedTable
     */
    private GeneratedJavaFile generateServiceImplJavaFile()
    {
        Boolean beanAware = Boolean.valueOf(properties.getProperty("beanAware"));
        
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        
        FullyQualifiedJavaType qualifiedExampleClass = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        
        FullyQualifiedJavaType qualifiedImplRootClass = new FullyQualifiedJavaType(GenericServiceImpl.class.getSimpleName());

        qualifiedImplRootClass.addTypeArgument(new FullyQualifiedJavaType(qualifiedExampleClass.getShortName()));
        qualifiedImplRootClass.addTypeArgument(new FullyQualifiedJavaType(domainObjectName));

        TopLevelClass serviceImpl = new TopLevelClass(new FullyQualifiedJavaType(serviceImplPackage + "." + domainObjectName + "ServiceImpl"));

        serviceImpl.addImportedType(new FullyQualifiedJavaType(GenericServiceImpl.class.getCanonicalName()));
        serviceImpl.addImportedType(new FullyQualifiedJavaType(servicePackage + "." + domainObjectName + "Service"));
        serviceImpl.addImportedType(qualifiedExampleClass);
        serviceImpl.addImportedType(new FullyQualifiedJavaType(context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + domainObjectName));

        serviceImpl.setVisibility(JavaVisibility.PUBLIC);
        serviceImpl.addSuperInterface(new FullyQualifiedJavaType(domainObjectName + "Service"));
        serviceImpl.setSuperClass(qualifiedImplRootClass);
        
        if(beanAware)
        {
            serviceImpl.addAnnotation("@Service");
            serviceImpl.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
            
                       
            Method method = new Method("setMapper");
            
            method.addAnnotation("@Autowired");
            method.setVisibility(JavaVisibility.PRIVATE);
            method.addParameter(new Parameter(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()).getShortName()), "mapper"));
            method.addBodyLine("super.setMapper(mapper);");
            
            serviceImpl.addMethod(method);

            serviceImpl.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
            serviceImpl.addImportedType(new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()));
        }

        return new GeneratedJavaFile(serviceImpl, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
    }

    @Override
    public boolean clientGenerated(Interface mapper, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        
        FullyQualifiedJavaType qualifiedExampleClass = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        
        FullyQualifiedJavaType qualifiedRootClass = new FullyQualifiedJavaType(GenericMapper.class.getSimpleName());

        qualifiedRootClass.addTypeArgument(new FullyQualifiedJavaType(qualifiedExampleClass.getShortName()));
        qualifiedRootClass.addTypeArgument(new FullyQualifiedJavaType(domainObjectName));

        mapper.addImportedType(new FullyQualifiedJavaType(GenericMapper.class.getCanonicalName()));
        mapper.addImportedType(qualifiedExampleClass);
        mapper.addImportedType(new FullyQualifiedJavaType(context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + domainObjectName));

        mapper.setVisibility(JavaVisibility.PUBLIC);
        mapper.addSuperInterface(qualifiedRootClass);
        
        for (Iterator<Method> iterator = mapper.getMethods().iterator(); iterator.hasNext();)
        {
            Method method = iterator.next();
            
            if(!method.getName().contains("PrimaryKey"))
            {
                iterator.remove();
            }
        }
        
        return true;
    }

    /**
     * 更新继承 GenericCriteria
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     * @see org.mybatis.generator.api.PluginAdapter#modelExampleClassGenerated(org.mybatis.generator.api.dom.java.TopLevelClass, org.mybatis.generator.api.IntrospectedTable)
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        FullyQualifiedJavaType qualifiedCriteriaClass = new FullyQualifiedJavaType(introspectedTable.getExampleType() + ".Criteria");
        
        FullyQualifiedJavaType qualifiedRootClass = new FullyQualifiedJavaType(GenericCriteria.class.getSimpleName());

        qualifiedRootClass.addTypeArgument(new FullyQualifiedJavaType("Criteria"));

        topLevelClass.addImportedType(new FullyQualifiedJavaType(GenericCriteria.class.getCanonicalName()));
        topLevelClass.addImportedType(qualifiedCriteriaClass);

        topLevelClass.setSuperClass(qualifiedRootClass);

        //
        topLevelClass.getFields().clear();

        //
        for (Iterator<Method> iterator = topLevelClass.getMethods().iterator(); iterator.hasNext();)
        {
            Method method = iterator.next();

            if (method.getName().equals("createCriteriaInternal"))
            {
                method.addAnnotation("@Override");

                continue;
            }
            //
            iterator.remove();
        }

        //
        for (Iterator<InnerClass> iterator = topLevelClass.getInnerClasses().iterator(); iterator.hasNext();)
        {
            InnerClass innerClass = iterator.next();

            if (innerClass.getType().getShortName().equals("Criterion"))
            {
                iterator.remove();

                continue;
            }
        }

        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable)
    {
        this.introspectedTable = introspectedTable;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles()
    {
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>();

        generatedJavaFiles.add(generateServiceJavaFile());

        generatedJavaFiles.add(generateServiceImplJavaFile());

        return generatedJavaFiles;
    }
}
