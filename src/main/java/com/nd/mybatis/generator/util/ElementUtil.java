/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.JavaBeansUtil;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public abstract class ElementUtil
{
    /**
     * @param topLevelClass
     * @param fullyQualifiedJavaType
     * @param property
     * @param setter
     * @param getter
     */
    public static void newField(TopLevelClass topLevelClass, FullyQualifiedJavaType fullyQualifiedJavaType, String property, boolean setter, boolean getter)
    {
        newField(topLevelClass, fullyQualifiedJavaType, property, JavaVisibility.PRIVATE);
        
        newGetter(topLevelClass, fullyQualifiedJavaType, property, JavaVisibility.PUBLIC);
        
        newSetter(topLevelClass, fullyQualifiedJavaType, property, JavaVisibility.PUBLIC);
    }

    /**
     * @param topLevelClass
     * @param fullyQualifiedJavaType
     * @param property
     * @param visibility
     */
    public static void newField(TopLevelClass topLevelClass, FullyQualifiedJavaType fullyQualifiedJavaType, String property, JavaVisibility visibility)
    {
        FullyQualifiedJavaType shortQualifiedJavaType = new FullyQualifiedJavaType(fullyQualifiedJavaType.getShortName());
        
        Field field = new Field(property, shortQualifiedJavaType);
        field.setVisibility(visibility);
        
        topLevelClass.addField(field);
        
        topLevelClass.addImportedType(fullyQualifiedJavaType);
    }
    
    /**
     * @param topLevelClass
     * @param fullyQualifiedJavaType
     * @param property
     * @param visibility
     */
    public static void newSetter(TopLevelClass topLevelClass, FullyQualifiedJavaType fullyQualifiedJavaType, String property, JavaVisibility visibility)
    {
        FullyQualifiedJavaType shortQualifiedJavaType = new FullyQualifiedJavaType(fullyQualifiedJavaType.getShortName());
        
        Method setterMethod = new Method(JavaBeansUtil.getSetterMethodName(property));
        setterMethod.addParameter(new Parameter(shortQualifiedJavaType, property));
        setterMethod.setVisibility(visibility);
        setterMethod.addBodyLine("this." + property + " = " + property + ";");
        
        topLevelClass.addMethod(setterMethod);
        
        topLevelClass.addImportedType(fullyQualifiedJavaType);
    }
    
    /**
     * @param topLevelClass
     * @param fullyQualifiedJavaType
     * @param property
     * @param visibility
     */
    public static void newGetter(TopLevelClass topLevelClass, FullyQualifiedJavaType fullyQualifiedJavaType, String property, JavaVisibility visibility)
    {
        FullyQualifiedJavaType shortQualifiedJavaType = new FullyQualifiedJavaType(fullyQualifiedJavaType.getShortName());
        
        Method getterMethod = new Method(JavaBeansUtil.getGetterMethodName(property, fullyQualifiedJavaType));
        getterMethod.setVisibility(visibility);
        getterMethod.setReturnType(shortQualifiedJavaType);
        getterMethod.addBodyLine("return " + property + ";");
        
        topLevelClass.addMethod(getterMethod);
        
        topLevelClass.addImportedType(fullyQualifiedJavaType);
    }
    

    /**
     * @param element
     * @param string
     * @param attribute
     * @return
     */
    public static boolean xmlElementMatcher(Element element, String name, Attribute... attributes)
    {
        if (element instanceof XmlElement)
        {
            XmlElement xmlElement = (XmlElement) element;

            if (!Objects.equals(name, xmlElement.getName()))
            {
                return false;
            }

            if (attributes == null || attributes.length <= 0)
            {
                return true;
            }

            Map<String, String> elementAttribute = new HashMap<String, String>();
            for (Iterator<Attribute> iterator = xmlElement.getAttributes().iterator(); iterator.hasNext();)
            {
                Attribute attribute = iterator.next();

                elementAttribute.put(attribute.getName(), attribute.getValue());
            }

            for (Attribute attribute : attributes)
            {
                if(!elementAttribute.containsKey(attribute.getName()) || !Objects.equals(elementAttribute.get(attribute.getName()), attribute.getValue()))
                {
                    return false;
                }
            }
            
            return true;
        }

        return false;
    }
    
    /**
     * @param document
     * @param name
     * @param attributes
     * @return
     */
    public static int xmlElementIndex(Document document, String name, Attribute... attributes)
    {
        int index = 0;
        
        for (Iterator<Element> iterator = document.getRootElement().getElements().iterator(); iterator.hasNext();)
        {
            Element element = iterator.next();
            
            if(xmlElementMatcher(element, name, attributes))
            {
                return index;
            }

            index++;
        }
        
        return -1;
    }
}
