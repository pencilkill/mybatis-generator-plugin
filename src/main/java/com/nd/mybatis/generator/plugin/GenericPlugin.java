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
    private FullyQualifiedJavaType qualifiedRecordType;

    private FullyQualifiedJavaType qualifiedCriteriaType;
    
    private FullyQualifiedJavaType qualifiedExampleType;

    private FullyQualifiedJavaType qualifiedMapperType;

    private FullyQualifiedJavaType qualifiedServiceType;

    private FullyQualifiedJavaType qualifiedServiceImplType;


    /**
     * @param warnings
     * @return
     * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
     */
    @Override
    public boolean validate(List<String> warnings)
    {
        if (!StringUtility.stringHasValue(properties.getProperty("servicePackage")))
        {
            warnings.add("ServicePackage must not be empty");

            return false;
        }

        if (!StringUtility.stringHasValue(properties.getProperty("serviceImplPackage")))
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
        FullyQualifiedJavaType qualifiedRootServiceType = new FullyQualifiedJavaType(GenericService.class.getSimpleName());

        qualifiedRootServiceType.addTypeArgument(new FullyQualifiedJavaType(qualifiedExampleType.getShortName()));
        qualifiedRootServiceType.addTypeArgument(new FullyQualifiedJavaType(qualifiedRecordType.getShortName()));

        Interface service = new Interface(qualifiedServiceType);

        service.addImportedType(new FullyQualifiedJavaType(GenericService.class.getCanonicalName()));
        service.addImportedType(qualifiedExampleType);
        service.addImportedType(qualifiedRecordType);

        service.setVisibility(JavaVisibility.PUBLIC);
        service.addSuperInterface(qualifiedRootServiceType);
        
        return new GeneratedJavaFile(service, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
    }

    /**
     * @param introspectedTable
     */
    private GeneratedJavaFile generateServiceImplJavaFile()
    {
        FullyQualifiedJavaType qualifiedRootImplType = new FullyQualifiedJavaType(GenericServiceImpl.class.getSimpleName());

        qualifiedRootImplType.addTypeArgument(new FullyQualifiedJavaType(qualifiedExampleType.getShortName()));
        qualifiedRootImplType.addTypeArgument(new FullyQualifiedJavaType(qualifiedRecordType.getShortName()));

        TopLevelClass serviceImpl = new TopLevelClass(qualifiedServiceImplType);

        serviceImpl.addImportedType(new FullyQualifiedJavaType(GenericServiceImpl.class.getCanonicalName()));
        serviceImpl.addImportedType(qualifiedServiceType);
        serviceImpl.addImportedType(qualifiedExampleType);
        serviceImpl.addImportedType(qualifiedRecordType);

        serviceImpl.setVisibility(JavaVisibility.PUBLIC);
        serviceImpl.addSuperInterface(new FullyQualifiedJavaType(qualifiedServiceType.getShortName()));
        serviceImpl.setSuperClass(qualifiedRootImplType);
        
        if(Boolean.valueOf(properties.getProperty("beanAware")))
        {
            serviceImpl.addAnnotation("@Service");
            serviceImpl.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
            
                       
            Method method = new Method("setMapper");
            
            method.addAnnotation("@Autowired");
            method.setVisibility(JavaVisibility.PRIVATE);
            method.addParameter(new Parameter(new FullyQualifiedJavaType(qualifiedMapperType.getShortName()), "mapper"));
            method.addBodyLine("super.setMapper(mapper);");
            
            serviceImpl.addMethod(method);

            serviceImpl.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
            serviceImpl.addImportedType(qualifiedMapperType);
        }

        return new GeneratedJavaFile(serviceImpl, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
    }

    @Override
    public boolean clientGenerated(Interface mapper, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        FullyQualifiedJavaType qualifiedRootMapperType = new FullyQualifiedJavaType(GenericMapper.class.getSimpleName());

        qualifiedRootMapperType.addTypeArgument(new FullyQualifiedJavaType(qualifiedExampleType.getShortName()));
        qualifiedRootMapperType.addTypeArgument(new FullyQualifiedJavaType(qualifiedRecordType.getShortName()));

        mapper.addImportedType(new FullyQualifiedJavaType(GenericMapper.class.getCanonicalName()));
        mapper.addImportedType(qualifiedExampleType);
        mapper.addImportedType(qualifiedRecordType);

        mapper.setVisibility(JavaVisibility.PUBLIC);
        mapper.addSuperInterface(qualifiedRootMapperType);
        
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
        FullyQualifiedJavaType qualifiedRootCriteriaType = new FullyQualifiedJavaType(GenericCriteria.class.getSimpleName());

        qualifiedRootCriteriaType.addTypeArgument(new FullyQualifiedJavaType(qualifiedCriteriaType.getShortName()));

        topLevelClass.addImportedType(new FullyQualifiedJavaType(GenericCriteria.class.getCanonicalName()));
        topLevelClass.addImportedType(qualifiedCriteriaType);

        topLevelClass.setSuperClass(qualifiedRootCriteriaType);
        
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
        this.qualifiedRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        
        this.qualifiedCriteriaType = new FullyQualifiedJavaType(introspectedTable.getExampleType() + ".Criteria");
        
        this.qualifiedExampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        
        this.qualifiedMapperType =  new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        
        this.qualifiedServiceType = new FullyQualifiedJavaType(properties.getProperty("servicePackage") + "." + qualifiedRecordType.getShortName() + "Service");
        
        this.qualifiedServiceImplType = new FullyQualifiedJavaType(properties.getProperty("serviceImplPackage") + "." + qualifiedRecordType.getShortName() + "ServiceImpl");
        
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
