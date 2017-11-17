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
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import com.nd.mybatis.generator.util.JavaElementUtil;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class ExampleGroupByClausePlugin extends PluginAdapter
{
    private static final String GROUP_BY_CLAUSE_PROPERTY_NAME =  "groupByClause";

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
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
    {
        JavaElementUtil.newField(topLevelClass, FullyQualifiedJavaType.getStringInstance(), GROUP_BY_CLAUSE_PROPERTY_NAME, true, true);
        
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
        int index = 0;
        //
        for (Element el : element.getElements())
        {
            index++;
            
            if(el.getFormattedContent(0).contains(introspectedTable.getExampleWhereClauseId()))
            {
                break;
            }
        }
        //
        if(index > 0)
        {
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test", GROUP_BY_CLAUSE_PROPERTY_NAME + " != null"));
            ifElement.addElement(new TextElement("group by ${" + GROUP_BY_CLAUSE_PROPERTY_NAME + "}"));
            
            element.addElement(index, ifElement);
        }
    }
}
