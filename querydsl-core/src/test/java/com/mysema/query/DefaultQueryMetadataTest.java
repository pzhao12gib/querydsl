/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.mysema.query.QueryFlag.Position;
import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.expr.Param;
import com.mysema.query.types.path.StringPath;

public class DefaultQueryMetadataTest {

    private final QueryMetadata metadata = new DefaultQueryMetadata();

    private final StringPath str = new StringPath("str");
    
    private final StringPath str2 = new StringPath("str2");

    @Test
    public void AddWhere(){
        metadata.addWhere(new BooleanBuilder());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Validation(){
        metadata.addGroupBy(str);
    }
    
    @Test
    public void GetGroupBy() {
        metadata.addJoin(JoinType.DEFAULT, str);
        metadata.addGroupBy(str);
        assertEquals(Arrays.asList(str), metadata.getGroupBy());
    }

    @Test
    public void GetHaving() {
        metadata.addJoin(JoinType.DEFAULT, str);
        metadata.addHaving(str.isNotNull());
        assertEquals(str.isNotNull(), metadata.getHaving());
    }

    @Test
    public void GetJoins() {
        metadata.addJoin(JoinType.DEFAULT, str);
        assertEquals(Arrays.asList(new JoinExpression(JoinType.DEFAULT, str)),metadata.getJoins());
    }
    
    @Test
    public void GetJoins2() {
        metadata.addJoin(new JoinExpression(JoinType.DEFAULT, str));
        assertEquals(Arrays.asList(new JoinExpression(JoinType.DEFAULT, str)),metadata.getJoins());
    }

    @Test
    public void GetModifiers() {
        QueryModifiers modifiers = new QueryModifiers(1l,2l);
        metadata.setModifiers(modifiers);
        assertEquals(modifiers, metadata.getModifiers());
    }

    @Test
    public void setLimit(){
        QueryModifiers modifiers = new QueryModifiers(1l,2l);
        metadata.setModifiers(modifiers);
        metadata.setLimit(3l);

        assertEquals(Long.valueOf(3l), metadata.getModifiers().getLimit());
        assertEquals(Long.valueOf(2l), metadata.getModifiers().getOffset());
    }

    @Test
    public void setOffset(){
        QueryModifiers modifiers = new QueryModifiers(1l,1l);
        metadata.setModifiers(modifiers);
        metadata.setOffset(2l);

        assertEquals(Long.valueOf(1l), metadata.getModifiers().getLimit());
        assertEquals(Long.valueOf(2l), metadata.getModifiers().getOffset());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void GetOrderBy() {
        metadata.addJoin(JoinType.DEFAULT, str);
        metadata.addOrderBy(str.asc());
        metadata.addOrderBy(str.desc());
        assertEquals(Arrays.asList(str.asc(),str.desc()), metadata.getOrderBy());
    }

    @Test
    public void GetProjection() {
        metadata.addJoin(JoinType.DEFAULT, str);
        metadata.addProjection(str, str.append("abc"));
        assertEquals(Arrays.asList(str, str.append("abc")), metadata.getProjection());
    }

    @Test
    public void GetWhere() {
        metadata.addJoin(JoinType.DEFAULT, str);
        metadata.addWhere(str.eq("b"), str.isNotEmpty());
        assertEquals(str.eq("b").and(str.isNotEmpty()), metadata.getWhere());
    }

    @Test
    public void IsDistinct() {
        assertFalse(metadata.isDistinct());
        metadata.setDistinct(true);
        assertTrue(metadata.isDistinct());
    }

    @Test
    public void IsUnique() {
        assertFalse(metadata.isUnique());
        metadata.setUnique(true);
        assertTrue(metadata.isUnique());
    }

    @Test
    public void Clone(){
        metadata.addJoin(JoinType.DEFAULT, str);
        metadata.addGroupBy(str);
        metadata.addHaving(str.isNotNull());
        metadata.addJoin(JoinType.DEFAULT, str2);
        QueryModifiers modifiers = new QueryModifiers(1l,2l);
        metadata.setModifiers(modifiers);
        metadata.addOrderBy(str.asc());
        metadata.addProjection(str, str.append("abc"));
        metadata.addWhere(str.eq("b"), str.isNotEmpty());

        QueryMetadata clone = metadata.clone();
        assertEquals(metadata.getGroupBy(), clone.getGroupBy());
        assertEquals(metadata.getHaving(), clone.getHaving());
        assertEquals(metadata.getJoins(), clone.getJoins());
        assertEquals(metadata.getModifiers(), clone.getModifiers());
        assertEquals(metadata.getOrderBy(), clone.getOrderBy());
        assertEquals(metadata.getProjection(), clone.getProjection());
        assertEquals(metadata.getWhere(), clone.getWhere());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void SetParam(){
        metadata.setParam(new Param(String.class, "str"), ConstantImpl.create("X"));
        assertEquals(1, metadata.getParams().size());
        assertTrue(metadata.getParams().get(new Param(String.class, "str")).equals(ConstantImpl.create("X")));
    }
    
    @Test
    public void AddFlag(){
        QueryFlag flag = new QueryFlag(Position.START, "X");
        metadata.addFlag(flag);
        assertTrue(metadata.hasFlag(flag));
    }
}
