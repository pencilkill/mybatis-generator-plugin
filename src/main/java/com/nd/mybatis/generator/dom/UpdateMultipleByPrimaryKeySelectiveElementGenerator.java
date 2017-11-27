/**
 * @copyright Copyright 1999-2017 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.mybatis.generator.dom;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 *
 */
public class UpdateMultipleByPrimaryKeySelectiveElementGenerator extends AbstractXmlElementGenerator
{
    private boolean isCompositePrimaryKey;
    
    private int index;

    public UpdateMultipleByPrimaryKeySelectiveElementGenerator(int index, boolean isCompositePrimaryKey)
    {
        super();
        
        this.index = index;
        this.isCompositePrimaryKey = isCompositePrimaryKey;
    }

    @Override
    public void addElements(XmlElement parentElement)
    {
        XmlElement answer = new XmlElement("update");

        answer.addAttribute(new Attribute("id", introspectedTable.getUpdateByPrimaryKeySelectiveStatementId()));

        String parameterType;

        if (introspectedTable.getRules().generateRecordWithBLOBsClass())
        {
            parameterType = introspectedTable.getRecordWithBLOBsType();
        }
        else
        {
            parameterType = introspectedTable.getBaseRecordType();
        }

        answer.addAttribute(new Attribute("parameterType", parameterType));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set");
        
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns())
        {
            XmlElement isNotNullElement = new XmlElement("if");
            
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty("record."));
            sb.append(" != null");
            
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            
            dynamicElement.addElement(isNotNullElement);

            isNotNullElement.addElement(foreachColumnElement(introspectedColumn));
        }

        answer.addElement(wherePramaryKeyElement());
        
        answer.addElement(new TextElement(" in "));
        
        answer.addElement(foreachWherePramaryKeyCriteriaElement());

        if (context.getPlugins().sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer, introspectedTable))
        {
            parentElement.addElement(index, answer);
        }
    }

    private Element foreachColumnElement(IntrospectedColumn introspectedColumn)
    {
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "records"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("open", MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn) + " = case "));
        foreachElement.addAttribute(new Attribute("close", " else " + MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn) + " end, "));

        foreachElement.addElement(caseWhenColumnElement(introspectedColumn, "item."));

        return foreachElement;
    }

    private Element caseWhenColumnElement(IntrospectedColumn introspectedColumn, String parameterClausePrefix)
    {
        StringBuilder sb = new StringBuilder("when ");
        
        if(isCompositePrimaryKey)
        {
            sb.append("(");
        }

        boolean and = false;
        for (IntrospectedColumn introspectedKeyColumn : introspectedTable.getPrimaryKeyColumns())
        {
            if (and)
            {
                sb.append(" and ");
            }
            else
            {
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedKeyColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedKeyColumn));
        }
        
        if(isCompositePrimaryKey)
        {
            sb.append(")");
        }

        sb.append(" then ");
        sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, parameterClausePrefix));

        return new TextElement(sb.toString());
    }
    
    private Element wherePramaryKeyElement()
    {
        StringBuilder sb = new StringBuilder("where ");
        
        if(isCompositePrimaryKey)
        {
            sb.append("(");
        }
        
        boolean and = false;
        for (IntrospectedColumn introspectedKeyColumn : introspectedTable.getPrimaryKeyColumns())
        {
            if (and)
            {
                sb.append(" and ");
            }
            else
            {
                and = true;
            }
            
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedKeyColumn));
        }
        
        if(isCompositePrimaryKey)
        {
            sb.append(")");
        }
        
        return new TextElement(sb.toString());
    }
    

    private Element foreachWherePramaryKeyCriteriaElement()
    {
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "records"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("open", "("));
        foreachElement.addAttribute(new Attribute("close", ")"));
        foreachElement.addAttribute(new Attribute("separator", ", "));

        foreachElement.addElement(wherePramaryKeyCriteriaElement("item."));

        return foreachElement;
    }
    
    private Element wherePramaryKeyCriteriaElement(String parameterClausePrefix)
    {
        StringBuilder sb = new StringBuilder("");
        
        if(isCompositePrimaryKey)
        {
            sb.append("(");
        }

        boolean and = false;
        for (IntrospectedColumn introspectedKeyColumn : introspectedTable.getPrimaryKeyColumns())
        {
            if (and)
            {
                sb.append(", ");
            }
            else
            {
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedKeyColumn, parameterClausePrefix));
        }

        if(isCompositePrimaryKey)
        {
            sb.append(")");
        }

        return new TextElement(sb.toString());
    }
}
