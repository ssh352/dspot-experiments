package com.github.mustachejava;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Function;
import junit.framework.Assert;
import junit.framework.ComparisonFailure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AmplPreTranslateTest {
    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString5_failAssert0_add3193_failAssert0_literalMutationString13847_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(reader, file, "{[", "A");
                            }
                        };

                        @Override
                        public Mustache compile(Reader reader, String file, String sm, String em) {
                            return super.compile(reader, file, "{[", "]}");
                        }

                        @Override
                        protected Function<String, Mustache> getMustacheCacheFunction() {
                            return ( template) -> {
                                Mustache compile = mp.compile(template);
                                compile.init();
                                return compile;
                            };
                        }
                    };
                    Mustache m = mf.compile("");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                    m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString5 should have thrown MustacheException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString5_failAssert0_add3193 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString5_failAssert0_add3193_failAssert0_literalMutationString13847 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_add3298_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            mp.compile(template);
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_add3298 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0null4142_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", null);
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0null4142 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0null4142_failAssert0_literalMutationString5736_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(reader, file, "{[", null);
                            }
                        };

                        @Override
                        public Mustache compile(Reader reader, String file, String sm, String em) {
                            return super.compile(reader, file, "{[", "]}");
                        }

                        @Override
                        protected Function<String, Mustache> getMustacheCacheFunction() {
                            return ( template) -> {
                                Mustache compile = mp.compile(template);
                                compile.init();
                                return compile;
                            };
                        }
                    };
                    Mustache m = mf.compile("");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), "page1.txt");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0null4142 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0null4142_failAssert0_literalMutationString5736 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory() {
                MustacheParser mp = new MustacheParser(this) {
                    @Override
                    public Mustache compile(Reader reader, String file) {
                        return super.compile(reader, file, "{[", "]}");
                    }
                };

                @Override
                public Mustache compile(Reader reader, String file, String sm, String em) {
                    return super.compile(reader, file, "{[", "]}");
                }

                @Override
                protected Function<String, Mustache> getMustacheCacheFunction() {
                    return ( template) -> {
                        Mustache compile = mp.compile(template);
                        compile.init();
                        return compile;
                    };
                }
            };
            Mustache m = mf.compile("");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                Function i = ( input) -> "{{test}} Translate";
            }).close();
            Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
            mf = new DefaultMustacheFactory();
            m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {
                boolean show = true;

                String test = "Now";
            }).close();
            Assert.assertEquals("Now Translate\n", sw.toString());
            org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_literalMutationString1631_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("Q([c[L<J^zYx| DxTW 5<O,^t6UXn],Z{ESl =", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1631 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[Q([c[L<J^zYx| DxTW 5<O,^t6UXn],Z{ESl =]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0null4142_failAssert0_add15086_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(reader, file, "{[", null);
                            }
                        };

                        @Override
                        public Mustache compile(Reader reader, String file, String sm, String em) {
                            return super.compile(reader, file, "{[", "]}");
                        }

                        @Override
                        protected Function<String, Mustache> getMustacheCacheFunction() {
                            return ( template) -> {
                                Mustache compile = mp.compile(template);
                                compile.init();
                                return compile;
                            };
                        }
                    };
                    Mustache m = mf.compile("");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    });
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0null4142 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0null4142_failAssert0_add15086 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }
}
