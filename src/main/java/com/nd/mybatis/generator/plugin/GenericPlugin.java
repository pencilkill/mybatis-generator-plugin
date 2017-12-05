/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InitializationBlock;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
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
        
        Set<String> declaredMethods = new HashSet<String>();
        for (java.lang.reflect.Method method : GenericMapper.class.getDeclaredMethods())
        {
            declaredMethods.add(method.getName());
        }
        
        for (Iterator<Method> iterator = mapper.getMethods().iterator(); iterator.hasNext();)
        {
            Method method = iterator.next();
            
            if(declaredMethods.contains(method.getName()))
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
        Method method = new Method();
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("createCriteriaInternal");
        method.setReturnType(qualifiedCriteriaType);
        method.addBodyLine("return new " + qualifiedCriteriaType.getShortName() + "();");
        
        //
        topLevelClass.getMethods().clear();
        
        topLevelClass.addMethod(method);
        
        //
        InnerClass rewriteGeneratedCriteriaClass = rewriteGeneratedCriteriaClass(topLevelClass);
        
        if(rewriteGeneratedCriteriaClass != null)
        {
            topLevelClass.getInnerClasses().clear();
        
            topLevelClass.addInnerClass(rewriteGeneratedCriteriaClass);
        }

        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable)
    {
        this.qualifiedRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        
        this.qualifiedCriteriaType = new FullyQualifiedJavaType(introspectedTable.getExampleType() + "." + FullyQualifiedJavaType.getCriteriaInstance().getShortName());
        
        this.qualifiedExampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        
        this.qualifiedMapperType =  new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        
        this.qualifiedServiceType = new FullyQualifiedJavaType(properties.getProperty("servicePackage") + "." + qualifiedRecordType.getShortName() + "Service");
        
        this.qualifiedServiceImplType = new FullyQualifiedJavaType(properties.getProperty("serviceImplPackage") + "." + qualifiedRecordType.getShortName() + "ServiceImpl");
        
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable)
    {
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>();

        generatedJavaFiles.add(generateServiceJavaFile());

        generatedJavaFiles.add(generateServiceImplJavaFile());

        return generatedJavaFiles;
    }
    
    private InnerClass rewriteGeneratedCriteriaClass(TopLevelClass topLevelClass)
    {
        for (Iterator<InnerClass> classIterator = topLevelClass.getInnerClasses().iterator(); classIterator.hasNext();)
        {
            InnerClass innerClass = classIterator.next();
            
            if (FullyQualifiedJavaType.getGeneratedCriteriaInstance().getShortName().equals(innerClass.getType().getShortName()))
            {
                innerClass.setAbstract(false);
                innerClass.setVisibility(JavaVisibility.PUBLIC);
                
                for (Iterator<Method> methodIterator = innerClass.getMethods().iterator(); methodIterator.hasNext();)
                {
                    Method method = methodIterator.next();
                    
                    if(method.isConstructor())
                    {
                        method.setName(qualifiedCriteriaType.getShortName());
                    }
                }
                
                return new NamingInnerClass(innerClass, new FullyQualifiedJavaType(qualifiedCriteriaType.getShortName()));
            }
        }
        
        return null;
    }
    
    public static class NamingInnerClass extends InnerClass
    {
        private InnerClass original;

        public NamingInnerClass(InnerClass original, FullyQualifiedJavaType type)
        {
            super(type);
            
            this.original = original;
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getType()
         */
        @Override
        public FullyQualifiedJavaType getType()
        {
            return super.getType();
        }

        @Override
        public String getFormattedContent(int indentLevel) {
            StringBuilder sb = new StringBuilder();

            addFormattedJavadoc(sb, indentLevel);
            addFormattedAnnotations(sb, indentLevel);

            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append(getVisibility().getValue());

            if (isAbstract()) {
                sb.append("abstract "); //$NON-NLS-1$
            }

            if (isStatic()) {
                sb.append("static "); //$NON-NLS-1$
            }

            if (isFinal()) {
                sb.append("final "); //$NON-NLS-1$
            }

            sb.append("class "); //$NON-NLS-1$
            sb.append(getType().getShortName());

            if (getSuperClass() != null) {
                sb.append(" extends "); //$NON-NLS-1$
                sb.append(getSuperClass().getShortName());
            }

            if (getSuperInterfaceTypes().size() > 0) {
                sb.append(" implements "); //$NON-NLS-1$

                boolean comma = false;
                for (FullyQualifiedJavaType fqjt : getSuperInterfaceTypes()) {
                    if (comma) {
                        sb.append(", "); //$NON-NLS-1$
                    } else {
                        comma = true;
                    }

                    sb.append(fqjt.getShortName());
                }
            }

            sb.append(" {"); //$NON-NLS-1$
            indentLevel++;
            
            Iterator<Field> fldIter = getFields().iterator();
            while (fldIter.hasNext()) {
                OutputUtilities.newLine(sb);
                Field field = fldIter.next();
                sb.append(field.getFormattedContent(indentLevel));
                if (fldIter.hasNext()) {
                    OutputUtilities.newLine(sb);
                }
            }

            if (getInitializationBlocks().size() > 0) {
                OutputUtilities.newLine(sb);
            }

            Iterator<InitializationBlock> blkIter = getInitializationBlocks().iterator();
            while (blkIter.hasNext()) {
                OutputUtilities.newLine(sb);
                InitializationBlock initializationBlock = blkIter.next();
                sb.append(initializationBlock.getFormattedContent(indentLevel));
                if (blkIter.hasNext()) {
                    OutputUtilities.newLine(sb);
                }
            }

            if (getMethods().size() > 0) {
                OutputUtilities.newLine(sb);
            }

            Iterator<Method> mtdIter = getMethods().iterator();
            while (mtdIter.hasNext()) {
                OutputUtilities.newLine(sb);
                Method method = mtdIter.next();
                sb.append(method.getFormattedContent(indentLevel, false));
                if (mtdIter.hasNext()) {
                    OutputUtilities.newLine(sb);
                }
            }

            if (getInnerClasses().size() > 0) {
                OutputUtilities.newLine(sb);
            }
            Iterator<InnerClass> icIter = getInnerClasses().iterator();
            while (icIter.hasNext()) {
                OutputUtilities.newLine(sb);
                InnerClass innerClass = icIter.next();
                sb.append(innerClass.getFormattedContent(indentLevel));
                if (icIter.hasNext()) {
                    OutputUtilities.newLine(sb);
                }
            }

            if (getInnerEnums().size() > 0) {
                OutputUtilities.newLine(sb);
            }

            Iterator<InnerEnum> ieIter = getInnerEnums().iterator();
            while (ieIter.hasNext()) {
                OutputUtilities.newLine(sb);
                InnerEnum innerEnum = ieIter.next();
                sb.append(innerEnum.getFormattedContent(indentLevel));
                if (ieIter.hasNext()) {
                    OutputUtilities.newLine(sb);
                }
            }

            indentLevel--;
            OutputUtilities.newLine(sb);
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append('}');

            return sb.toString();
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.JavaElement#getJavaDocLines()
         */
        @Override
        public List<String> getJavaDocLines()
        {
            return original.getJavaDocLines();
        }

        /**
         * @param javaDocLine
         * @see org.mybatis.generator.api.dom.java.JavaElement#addJavaDocLine(java.lang.String)
         */
        @Override
        public void addJavaDocLine(String javaDocLine)
        {
            original.addJavaDocLine(javaDocLine);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.JavaElement#getAnnotations()
         */
        @Override
        public List<String> getAnnotations()
        {
            return original.getAnnotations();
        }

        /**
         * @param annotation
         * @see org.mybatis.generator.api.dom.java.JavaElement#addAnnotation(java.lang.String)
         */
        @Override
        public void addAnnotation(String annotation)
        {
            original.addAnnotation(annotation);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getFields()
         */
        @Override
        public List<Field> getFields()
        {
            return original.getFields();
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.JavaElement#getVisibility()
         */
        @Override
        public JavaVisibility getVisibility()
        {
            return original.getVisibility();
        }

        /**
         * @param field
         * @see org.mybatis.generator.api.dom.java.InnerClass#addField(org.mybatis.generator.api.dom.java.Field)
         */
        @Override
        public void addField(Field field)
        {
            original.addField(field);
        }

        /**
         * @param visibility
         * @see org.mybatis.generator.api.dom.java.JavaElement#setVisibility(org.mybatis.generator.api.dom.java.JavaVisibility)
         */
        @Override
        public void setVisibility(JavaVisibility visibility)
        {
            original.setVisibility(visibility);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getSuperClass()
         */
        @Override
        public FullyQualifiedJavaType getSuperClass()
        {
            return original.getSuperClass();
        }

        /**
         * @param superClass
         * @see org.mybatis.generator.api.dom.java.InnerClass#setSuperClass(org.mybatis.generator.api.dom.java.FullyQualifiedJavaType)
         */
        @Override
        public void setSuperClass(FullyQualifiedJavaType superClass)
        {
            original.setSuperClass(superClass);
        }

        /**
         *
         * @see org.mybatis.generator.api.dom.java.JavaElement#addSuppressTypeWarningsAnnotation()
         */
        @Override
        public void addSuppressTypeWarningsAnnotation()
        {
            original.addSuppressTypeWarningsAnnotation();
        }

        /**
         * @param sb
         * @param indentLevel
         * @see org.mybatis.generator.api.dom.java.JavaElement#addFormattedJavadoc(java.lang.StringBuilder, int)
         */
        @Override
        public void addFormattedJavadoc(StringBuilder sb, int indentLevel)
        {
            original.addFormattedJavadoc(sb, indentLevel);
        }

        /**
         * @param superClassType
         * @see org.mybatis.generator.api.dom.java.InnerClass#setSuperClass(java.lang.String)
         */
        @Override
        public void setSuperClass(String superClassType)
        {
            original.setSuperClass(superClassType);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getInnerClasses()
         */
        @Override
        public List<InnerClass> getInnerClasses()
        {
            return original.getInnerClasses();
        }

        /**
         * @param sb
         * @param indentLevel
         * @see org.mybatis.generator.api.dom.java.JavaElement#addFormattedAnnotations(java.lang.StringBuilder, int)
         */
        @Override
        public void addFormattedAnnotations(StringBuilder sb, int indentLevel)
        {
            original.addFormattedAnnotations(sb, indentLevel);
        }

        /**
         * @param innerClass
         * @see org.mybatis.generator.api.dom.java.InnerClass#addInnerClass(org.mybatis.generator.api.dom.java.InnerClass)
         */
        @Override
        public void addInnerClass(InnerClass innerClass)
        {
            original.addInnerClass(innerClass);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getInnerEnums()
         */
        @Override
        public List<InnerEnum> getInnerEnums()
        {
            return original.getInnerEnums();
        }

        /**
         * @param innerEnum
         * @see org.mybatis.generator.api.dom.java.InnerClass#addInnerEnum(org.mybatis.generator.api.dom.java.InnerEnum)
         */
        @Override
        public void addInnerEnum(InnerEnum innerEnum)
        {
            original.addInnerEnum(innerEnum);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.JavaElement#isFinal()
         */
        @Override
        public boolean isFinal()
        {
            return original.isFinal();
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getInitializationBlocks()
         */
        @Override
        public List<InitializationBlock> getInitializationBlocks()
        {
            return original.getInitializationBlocks();
        }

        /**
         * @param isFinal
         * @see org.mybatis.generator.api.dom.java.JavaElement#setFinal(boolean)
         */
        @Override
        public void setFinal(boolean isFinal)
        {
            original.setFinal(isFinal);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.JavaElement#isStatic()
         */
        @Override
        public boolean isStatic()
        {
            return original.isStatic();
        }

        /**
         * @param initializationBlock
         * @see org.mybatis.generator.api.dom.java.InnerClass#addInitializationBlock(org.mybatis.generator.api.dom.java.InitializationBlock)
         */
        @Override
        public void addInitializationBlock(InitializationBlock initializationBlock)
        {
            original.addInitializationBlock(initializationBlock);
        }

        /**
         * @param isStatic
         * @see org.mybatis.generator.api.dom.java.JavaElement#setStatic(boolean)
         */
        @Override
        public void setStatic(boolean isStatic)
        {
            original.setStatic(isStatic);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getSuperInterfaceTypes()
         */
        @Override
        public Set<FullyQualifiedJavaType> getSuperInterfaceTypes()
        {
            return original.getSuperInterfaceTypes();
        }

        /**
         * @param superInterface
         * @see org.mybatis.generator.api.dom.java.InnerClass#addSuperInterface(org.mybatis.generator.api.dom.java.FullyQualifiedJavaType)
         */
        @Override
        public void addSuperInterface(FullyQualifiedJavaType superInterface)
        {
            original.addSuperInterface(superInterface);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#getMethods()
         */
        @Override
        public List<Method> getMethods()
        {
            return original.getMethods();
        }

        /**
         * @param method
         * @see org.mybatis.generator.api.dom.java.InnerClass#addMethod(org.mybatis.generator.api.dom.java.Method)
         */
        @Override
        public void addMethod(Method method)
        {
            original.addMethod(method);
        }

        /**
         * @return
         * @see org.mybatis.generator.api.dom.java.InnerClass#isAbstract()
         */
        @Override
        public boolean isAbstract()
        {
            return original.isAbstract();
        }

        /**
         * @param isAbtract
         * @see org.mybatis.generator.api.dom.java.InnerClass#setAbstract(boolean)
         */
        @Override
        public void setAbstract(boolean isAbtract)
        {
            original.setAbstract(isAbtract);
        }
    }
}
