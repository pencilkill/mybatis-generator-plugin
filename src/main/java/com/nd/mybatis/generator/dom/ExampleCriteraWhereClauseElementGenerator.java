/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.dom;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class ExampleCriteraWhereClauseElementGenerator extends AbstractXmlElementGenerator
{
    public static final int CRITERIA_WHERE_CLAUSE_INDEX = -1;
    
    public static final String CRITERIA_WHERE_CLAUSE_ID = "Criteria_Where_Clause";
    
    private int index;
    
    private String clauseId;

    public ExampleCriteraWhereClauseElementGenerator()
    {
        this(CRITERIA_WHERE_CLAUSE_ID);
    }
    
    public ExampleCriteraWhereClauseElementGenerator(String clauseId)
    {
        this(clauseId, 0);
    }
    
    public ExampleCriteraWhereClauseElementGenerator(String clauseId, int index)
    {
        super();
        
        this.clauseId = clauseId;
        this.index = index;
    }

    @Override
    public void addElements(XmlElement parentElement)
    {
        XmlElement answer = new XmlElement("sql");

        answer.addAttribute(new Attribute("id", clauseId));

        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "criteria.valid"));
        answer.addElement(ifElement);

        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("prefix", "("));
        trimElement.addAttribute(new Attribute("suffix", ")"));
        trimElement.addAttribute(new Attribute("prefixOverrides", "and"));

        ifElement.addElement(trimElement);

        trimElement.addElement(getMiddleForEachElement(null));

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns())
        {
            if (stringHasValue(introspectedColumn.getTypeHandler()))
            {
                trimElement.addElement(getMiddleForEachElement(introspectedColumn));
            }
        }

        if (context.getPlugins().sqlMapExampleWhereClauseElementGenerated(answer, introspectedTable))
        {
            if(index >= 0)
            {
                parentElement.addElement(index, answer);
            }
            else
            {
                parentElement.addElement(answer);
            }
        }
    }

    /**
     * @see org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ExampleWhereClauseElementGenerator#getMiddleForEachElement(IntrospectedColumn)
     *
     * @param introspectedColumn
     * @return
     */
    private XmlElement getMiddleForEachElement(IntrospectedColumn introspectedColumn)
    {
        StringBuilder sb = new StringBuilder();
        String criteriaAttribute;
        boolean typeHandled;
        String typeHandlerString;
        if (introspectedColumn == null)
        {
            criteriaAttribute = "criteria.criteria";
            typeHandled = false;
            typeHandlerString = null;
        }
        else
        {
            sb.setLength(0);
            sb.append("criteria.");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append("Criteria");
            criteriaAttribute = sb.toString();

            typeHandled = true;

            sb.setLength(0);
            sb.append(",typeHandler=");
            sb.append(introspectedColumn.getTypeHandler());
            typeHandlerString = sb.toString();
        }

        XmlElement middleForEachElement = new XmlElement("foreach");
        middleForEachElement.addAttribute(new Attribute("collection", criteriaAttribute));
        middleForEachElement.addAttribute(new Attribute("item", "criterion"));

        XmlElement chooseElement = new XmlElement("choose");
        middleForEachElement.addElement(chooseElement);

        XmlElement when = new XmlElement("when");
        when.addAttribute(new Attribute("test", "criterion.noValue"));
        when.addElement(new TextElement("and ${criterion.condition}"));
        chooseElement.addElement(when);

        when = new XmlElement("when");
        when.addAttribute(new Attribute("test", "criterion.singleValue"));
        sb.setLength(0);
        sb.append("and ${criterion.condition} #{criterion.value");
        if (typeHandled)
        {
            sb.append(typeHandlerString);
        }
        sb.append('}');
        when.addElement(new TextElement(sb.toString()));
        chooseElement.addElement(when);

        when = new XmlElement("when");
        when.addAttribute(new Attribute("test", "criterion.betweenValue"));
        sb.setLength(0);
        sb.append("and ${criterion.condition} #{criterion.value");
        if (typeHandled)
        {
            sb.append(typeHandlerString);
        }
        sb.append("} and #{criterion.secondValue");
        if (typeHandled)
        {
            sb.append(typeHandlerString);
        }
        sb.append('}');
        when.addElement(new TextElement(sb.toString()));
        chooseElement.addElement(when);

        when = new XmlElement("when");
        when.addAttribute(new Attribute("test", "criterion.listValue"));
        when.addElement(new TextElement("and ${criterion.condition}"));
        XmlElement innerForEach = new XmlElement("foreach");
        innerForEach.addAttribute(new Attribute("collection", "criterion.value"));
        innerForEach.addAttribute(new Attribute("item", "listItem"));
        innerForEach.addAttribute(new Attribute("open", "("));
        innerForEach.addAttribute(new Attribute("close", ")"));
        innerForEach.addAttribute(new Attribute("separator", ","));
        sb.setLength(0);
        sb.append("#{listItem"); //$NON-NLS-1$
        if (typeHandled)
        {
            sb.append(typeHandlerString);
        }
        sb.append('}');
        innerForEach.addElement(new TextElement(sb.toString()));
        when.addElement(innerForEach);
        chooseElement.addElement(when);

        return middleForEachElement;
    }
}
