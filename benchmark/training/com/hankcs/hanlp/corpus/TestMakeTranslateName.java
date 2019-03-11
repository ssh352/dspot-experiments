/**
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/11/12 14:03</create-date>
 *
 * <copyright file="TestMakeTranslateName.java" company="????????????">
 * Copyright (c) 2003-2014, ????????????. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact ???????????? to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.corpus;


import com.hankcs.hanlp.dictionary.nr.TranslatedPersonDictionary;
import junit.framework.TestCase;


/**
 *
 *
 * @author hankcs
 */
// 
// public void testSeg() throws Exception
// {
// HanLP.Config.enableDebug();
// System.out.println(StandardTokenizer.segment("?????"));
// }
// 
// public void testNonRec() throws Exception
// {
// HanLP.Config.enableDebug();
// DijkstraSegment segment = new DijkstraSegment();
// segment.enableTranslatedNameRecognize(true);
// System.out.println(segment.seg("??????"));
// }
// 
// public void testHeadNRF() throws Exception
// {
// DijkstraSegment segment = new DijkstraSegment();
// segment.enableTranslatedNameRecognize(false);
// for (String name : IOUtil.readLineList("data/dictionary/person/nrf.txt"))
// {
// List<Term> termList = segment.seg(name);
// if (termList.get(0).nature != Nature.nrf)
// {
// System.out.println(name + " : " + termList);
// }
// }
// }
// 
// public void testDot() throws Exception
// {
// char c1 = '?';
// char c2 = '?';
// System.out.println(c1 == c2);
// }
// 
// public void testMakeDictionary() throws Exception
// {
// Set<String> wordSet = new TreeSet<String>();
// Pattern pattern = Pattern.compile("^[a-zA-Z]+ *(\\[.*?])? *([\\u4E00-\\u9FA5]+) ?[:??]");
// int found = 0;
// for (String line : IOUtil.readLineList("D:\\Doc\\???\\??????.txt"))
// {
// Matcher matcher = pattern.matcher(line);
// if (matcher.find())
// {
// wordSet.add(matcher.group(2));
// ++found;
// }
// }
// System.out.println("????" + found + "?");
// IOUtil.saveCollectionToTxt(wordSet, "data/dictionary/person/??????.txt");
// }
// 
// public void testRegex() throws Exception
// {
// Pattern pattern = Pattern.compile("^[a-zA-Z]+ (\\[.*?])? ?([\\u4E00-\\u9FA5]+) ?[:??]");
// String text = "Adey ???Adam?????????? \n" +
// "Adkin ???:Adarn??????????? \n" +
// "Adkins ????:???????Adkin,?????????(son of Adkin)??????? \n" +
// "Adlam [??????] ??????????????????????+??????(noble?protection?helmet) \n" +
// "Zena [???] ?????????????????(woman)? \n" +
// "Zenas [???] ?????????????????????????(gift of Zeus?the chief Greek god)? \n" +
// "Zenia [???]????Xeniq???? \n" +
// "Zenobia [???] ??????????????????????+???(the chief Greek god Zeus+life)? \n" +
// "Zillah [???] ?????????????????(shade)? \n" +
// "Zoe [???]??????????????????life?? \n" +
// "Zouch [??????] ???Such???? ";
// 
// Matcher matcher = pattern.matcher(text);
// if (matcher.find())
// {
// System.out.println(matcher.group(2));
// }
// }
// 
// public void testCombineCharAndName() throws Exception
// {
// TreeSet<String> wordSet = new TreeSet<String>();
// wordSet.addAll(IOUtil.readLineList("data/dictionary/person/????.txt"));
// wordSet.addAll(IOUtil.readLineList("data/dictionary/person/nrf.txt"));
// IOUtil.saveCollectionToTxt(wordSet, "data/dictionary/person/nrf.txt");
// }
public class TestMakeTranslateName extends TestCase {
    // public void testCombineOuterDictionary() throws Exception
    // {
    // String root = "D:\\JavaProjects\\SougouDownload\\data\\";
    // String[] pathArray = new String[]{"??????.txt", "????", "??????.txt", "?????.txt", "??????.txt", "?????.txt"};
    // Set<String> wordSet = new TreeSet<String>();
    // for (String path : pathArray)
    // {
    // path = root + path;
    // for (String word : IOUtil.readLineList(path))
    // {
    // word = word.replaceAll("[a-z]", "");
    // if (CoreDictionary.contains(word) || CustomDictionary.contains(word)) continue;
    // wordSet.add(word);
    // }
    // }
    // IOUtil.saveCollectionToTxt(wordSet, "data/dictionary/person/nrf.txt");
    // }
    // 
    // public void testSpiltToChar() throws Exception
    // {
    // String commonChar = "?-????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" +
    // "?-????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" +
    // "-??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" +
    // "??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" +
    // "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????";
    // Set<String> wordSet = new TreeSet<String>();
    // LinkedList<String> wordList = IOUtil.readLineList("data/dictionary/person/nrf.txt");
    // wordList.add(commonChar);
    // for (String word : wordList)
    // {
    // word = word.replaceAll("\\s", "");
    // for (char c : word.toCharArray())
    // {
    // wordSet.add(String.valueOf(c));
    // }
    // }
    // IOUtil.saveCollectionToTxt(wordSet, "data/dictionary/person/????.txt");
    // }
    // 
    public void testQuery() throws Exception {
        TestCase.assertTrue(TranslatedPersonDictionary.containsKey("??"));
        // HanLP.Config.enableDebug();
        // System.out.println(TranslatedPersonDictionary.containsKey("??"));
        // System.out.println(TranslatedPersonDictionary.containsKey("?"));
        // System.out.println(TranslatedPersonDictionary.containsKey("?"));
        // System.out.println(TranslatedPersonDictionary.containsKey("?"));
        // System.out.println(TranslatedPersonDictionary.containsKey("?"));
    }
}

