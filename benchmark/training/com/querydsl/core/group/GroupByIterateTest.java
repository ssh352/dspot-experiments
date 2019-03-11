/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.group;


import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class GroupByIterateTest extends AbstractGroupByTest {
    @Test
    public void group_order() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
    }

    @Test
    public void first_set_and_list() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Group group = results.get(1);
        Assert.assertEquals(toInt(1), group.getOne(AbstractGroupByTest.postId));
        Assert.assertEquals("post 1", group.getOne(AbstractGroupByTest.postName));
        Assert.assertEquals(toSet(1, 2, 3), group.getSet(AbstractGroupByTest.commentId));
        Assert.assertEquals(Arrays.asList("comment 1", "comment 2", "comment 3"), group.getList(AbstractGroupByTest.commentText));
    }

    @Test
    public void group_by_null() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Group group = results.get(0);
        Assert.assertNull(group.getOne(AbstractGroupByTest.postId));
        Assert.assertEquals("null post", group.getOne(AbstractGroupByTest.postName));
        Assert.assertEquals(toSet(7, 8), group.getSet(AbstractGroupByTest.commentId));
        Assert.assertEquals(Arrays.asList("comment 7", "comment 8"), group.getList(AbstractGroupByTest.commentText));
    }

    @Test(expected = NoSuchElementException.class)
    public void noSuchElementException() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Group group = results.get(1);
        group.getSet(AbstractGroupByTest.qComment);
    }

    @Test(expected = ClassCastException.class)
    public void classCastException() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Group group = results.get(1);
        group.getList(AbstractGroupByTest.commentId);
    }

    @Test
    public void map1() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.MAP_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Group group = results.get(1);
        Map<Integer, String> comments = group.getMap(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText);
        Assert.assertEquals(3, comments.size());
        Assert.assertEquals("comment 2", comments.get(2));
    }

    @Test
    public void map2() {
        CloseableIterator<Map<Integer, String>> resultsIt = AbstractGroupByTest.MAP2_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText)));
        List<Map<Integer, String>> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Map<Integer, String> comments = results.get(1);
        Assert.assertEquals(3, comments.size());
        Assert.assertEquals("comment 2", comments.get(2));
    }

    @Test
    public void map22() {
        CloseableIterator<Map<Integer, String>> results = AbstractGroupByTest.MAP2_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText)));
        List<Map<Integer, String>> actual = IteratorAdapter.asList(results);
        Object commentId = null;
        Map<Integer, String> comments = null;
        List<Map<Integer, String>> expected = new LinkedList<Map<Integer, String>>();
        for (Iterator<Tuple> iterator = AbstractGroupByTest.MAP2_RESULTS.iterate(); iterator.hasNext();) {
            Tuple tuple = iterator.next();
            Object[] array = tuple.toArray();
            if ((comments == null) || (!((commentId == (array[0])) || ((commentId != null) && (commentId.equals(array[0])))))) {
                comments = new LinkedHashMap<Integer, String>();
                expected.add(comments);
            }
            commentId = array[0];
            @SuppressWarnings("unchecked")
            Pair<Integer, String> pair = ((Pair<Integer, String>) (array[1]));
            comments.put(pair.getFirst(), pair.getSecond());
        }
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void map3() {
        CloseableIterator<Map<Integer, Map<Integer, String>>> results = AbstractGroupByTest.MAP3_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(map(AbstractGroupByTest.postId, map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText))));
        List<Map<Integer, Map<Integer, String>>> actual = IteratorAdapter.asList(results);
        Object postId = null;
        Map<Integer, Map<Integer, String>> posts = null;
        List<Map<Integer, Map<Integer, String>>> expected = new LinkedList<Map<Integer, Map<Integer, String>>>();
        for (Iterator<Tuple> iterator = AbstractGroupByTest.MAP3_RESULTS.iterate(); iterator.hasNext();) {
            Tuple tuple = iterator.next();
            Object[] array = tuple.toArray();
            if ((posts == null) || (!((postId == (array[0])) || ((postId != null) && (postId.equals(array[0])))))) {
                posts = new LinkedHashMap<Integer, Map<Integer, String>>();
                expected.add(posts);
            }
            postId = array[0];
            @SuppressWarnings("unchecked")
            Pair<Integer, Pair<Integer, String>> pair = ((Pair<Integer, Pair<Integer, String>>) (array[1]));
            Integer first = pair.getFirst();
            Map<Integer, String> comments = posts.get(first);
            if (comments == null) {
                comments = new LinkedHashMap<Integer, String>();
                posts.put(first, comments);
            }
            Pair<Integer, String> second = pair.getSecond();
            comments.put(second.getFirst(), second.getSecond());
        }
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void map4() {
        CloseableIterator<Map<Map<Integer, String>, String>> results = AbstractGroupByTest.MAP4_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(map(map(AbstractGroupByTest.postId, AbstractGroupByTest.commentText), AbstractGroupByTest.postName)));
        List<Map<Map<Integer, String>, String>> actual = IteratorAdapter.asList(results);
        Object commentId = null;
        Map<Map<Integer, String>, String> comments = null;
        List<Map<Map<Integer, String>, String>> expected = new LinkedList<Map<Map<Integer, String>, String>>();
        for (Iterator<Tuple> iterator = AbstractGroupByTest.MAP4_RESULTS.iterate(); iterator.hasNext();) {
            Tuple tuple = iterator.next();
            Object[] array = tuple.toArray();
            if ((comments == null) || (!((commentId == (array[0])) || ((commentId != null) && (commentId.equals(array[0])))))) {
                comments = new LinkedHashMap<Map<Integer, String>, String>();
                expected.add(comments);
            }
            commentId = array[0];
            @SuppressWarnings("unchecked")
            Pair<Pair<Integer, String>, String> pair = ((Pair<Pair<Integer, String>, String>) (array[1]));
            Pair<Integer, String> first = pair.getFirst();
            Map<Integer, String> posts = Collections.singletonMap(first.getFirst(), first.getSecond());
            comments.put(posts, pair.getSecond());
        }
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void array_access() {
        CloseableIterator<Group> resultsIt = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).iterate(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        List<Group> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Group group = results.get(1);
        Object[] array = group.toArray();
        Assert.assertEquals(toInt(1), array[0]);
        Assert.assertEquals("post 1", array[1]);
        Assert.assertEquals(toSet(1, 2, 3), array[2]);
        Assert.assertEquals(Arrays.asList("comment 1", "comment 2", "comment 3"), array[3]);
    }

    @Test
    public void transform_results() {
        CloseableIterator<Post> resultsIt = AbstractGroupByTest.POST_W_COMMENTS.transform(groupBy(AbstractGroupByTest.postId).iterate(Projections.constructor(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment))));
        List<Post> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(toInt(1), post.getId());
        Assert.assertEquals("post 1", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(1), AbstractGroupByTest.comment(2), AbstractGroupByTest.comment(3)), post.getComments());
    }

    @Test
    public void transform_as_bean() {
        CloseableIterator<Post> resultsIt = AbstractGroupByTest.POST_W_COMMENTS.transform(groupBy(AbstractGroupByTest.postId).iterate(Projections.bean(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment).as("comments"))));
        List<Post> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(4, results.size());
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(toInt(1), post.getId());
        Assert.assertEquals("post 1", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(1), AbstractGroupByTest.comment(2), AbstractGroupByTest.comment(3)), post.getComments());
    }

    @Test
    public void oneToOneToMany_projection() {
        CloseableIterator<User> resultsIt = AbstractGroupByTest.USERS_W_LATEST_POST_AND_COMMENTS.transform(groupBy(AbstractGroupByTest.userName).iterate(Projections.constructor(User.class, AbstractGroupByTest.userName, Projections.constructor(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment)))));
        List<User> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(2, results.size());
        User user = results.get(0);
        Post post = user.getLatestPost();
        Assert.assertEquals(toInt(2), post.getId());
        Assert.assertEquals("post 2", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(4), AbstractGroupByTest.comment(5)), post.getComments());
    }

    @Test
    public void oneToOneToMany_projection_as_bean() {
        CloseableIterator<User> resultsIt = AbstractGroupByTest.USERS_W_LATEST_POST_AND_COMMENTS.transform(groupBy(AbstractGroupByTest.userName).iterate(Projections.bean(User.class, AbstractGroupByTest.userName, Projections.bean(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment).as("comments")).as("latestPost"))));
        List<User> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(2, results.size());
        User user = results.get(0);
        Post post = user.getLatestPost();
        Assert.assertEquals(toInt(2), post.getId());
        Assert.assertEquals("post 2", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(4), AbstractGroupByTest.comment(5)), post.getComments());
    }

    @Test
    public void oneToOneToMany_projection_as_bean_and_constructor() {
        CloseableIterator<User> resultsIt = AbstractGroupByTest.USERS_W_LATEST_POST_AND_COMMENTS.transform(groupBy(AbstractGroupByTest.userName).iterate(Projections.bean(User.class, AbstractGroupByTest.userName, Projections.constructor(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment)).as("latestPost"))));
        List<User> results = IteratorAdapter.asList(resultsIt);
        Assert.assertEquals(2, results.size());
        User user = results.get(0);
        Post post = user.getLatestPost();
        Assert.assertEquals(toInt(2), post.getId());
        Assert.assertEquals("post 2", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(4), AbstractGroupByTest.comment(5)), post.getComments());
    }
}

