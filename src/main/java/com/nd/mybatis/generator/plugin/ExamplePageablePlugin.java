/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import com.nd.mybatis.generator.util.JavaElementUtil;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class ExamplePageablePlugin extends PluginAdapter
{
    private String limitStartName;

    private String limitEndName;
    
    /**
     * @param warnings
     * @return
     * @see org.mybatis.generator.api.Plugin#validate(java.util.List)
     */
    @Override
    public boolean validate(List<String> warnings)
    {
        return true;
    }
    
    @Override
    public void initialized(IntrospectedTable introspectedTable)
    {
        limitStartName = properties.getProperty("limitStartName", "limitStart");
        limitEndName = properties.getProperty("limitEndName", "limitEnd");
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        JavaElementUtil.newField(topLevelClass, new FullyQualifiedJavaType("java.lang.Integer"), limitStartName, true, true);
        
        JavaElementUtil.newField(topLevelClass, new FullyQualifiedJavaType("java.lang.Integer"), limitEndName, true, true);
        
        return true;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
    {
        addElement(element, introspectedTable);
        
        return true;
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
    {
        addElement(element, introspectedTable);
        
        return true;
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
    {
        addElement(element, introspectedTable);
        
        return true;
    }

    /**
     * @param element
     */
    private void addElement(XmlElement element, IntrospectedTable introspectedTable)
    {
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", limitStartName + " != null and " + limitStartName + ">-1"));
        ifElement.addElement(new TextElement("limit ${" + limitStartName + "} , ${" + limitEndName + "}"));
        
        element.addElement(element.getElements().size(), ifElement);
    }
}
