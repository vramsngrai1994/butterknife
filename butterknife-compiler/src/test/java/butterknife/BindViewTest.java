package butterknife;

import butterknife.compiler.ButterKnifeProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class BindViewTest {
  @Test public void bindingView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.BindView;",
            "public class Test extends Activity {",
            "    @BindView(1) View thing;",
            "}"
        ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void bindingViewFinalClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.BindView;",
            "public final class Test extends Activity {",
            "    @BindView(1) View thing;",
            "}"
        ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinder implements ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    return new Test_ViewBinding(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinding implements Unbinder {\n"
        + "  private Test target;\n"
        + "  public Test_ViewBinding(Test target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void bindingViewFinalClassWithBaseClass() {
    JavaFileObject baseSource = JavaFileObjects.forSourceString("test.Base",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.BindView;",
            "public class Base extends Activity {",
            "    @BindView(1) View thing;",
            "}"
        ));
    JavaFileObject testSource = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.BindView;",
            "public final class Test extends Base {",
            "    @BindView(1) View thing;",
            "}"
        ));

    JavaFileObject binderBaseSource = JavaFileObjects.forSourceString("test/Base_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Base_ViewBinder<T extends Base> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Base_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingBaseSource = JavaFileObjects.forSourceString("test/Base_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Base_ViewBinding<T extends Base> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Base_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binderTestSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinder extends Base_ViewBinder<Test> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, Test target, Object source) {\n"
        + "    return new Test_ViewBinding(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingTestSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public final class Test_ViewBinding extends Base_ViewBinding<Test> {\n"
        + "  public Test_ViewBinding(Test target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    Test target = this.target;\n"
        + "    super.unbind();\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSources()).that(Arrays.asList(baseSource, testSource))
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderBaseSource, bindingBaseSource, binderTestSource, bindingTestSource);
  }

  @Test public void bindingViewInnerClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Outer",
        Joiner.on('\n').join(
            "package test;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.BindView;",
            "public class Outer {",
            "  public static class Test extends Activity {",
            "    @BindView(1) View thing;",
            "  }",
            "}"
        ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Outer$Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Outer$Test_ViewBinder<T extends Outer.Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Outer$Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Outer$Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Outer$Test_ViewBinding<T extends Outer.Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Outer$Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void bindingViewUppercasePackageName() {
    JavaFileObject source = JavaFileObjects.forSourceString("com.Example.Test",
        Joiner.on('\n').join(
            "package com.Example;",
            "import android.app.Activity;",
            "import android.view.View;",
            "import butterknife.BindView;",
            "public class Test extends Activity {",
            "    @BindView(1) View thing;",
            "}"
        ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package com.Example;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package com.Example;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void bindingInterface() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    interface TestInterface {}",
        "    @BindView(1) TestInterface thing;",
        "}"
    ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredViewAsType(source, 1, \"field 'thing'\", Test.TestInterface.class);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void genericType() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.widget.EditText;",
        "import android.widget.TextView;",
        "import butterknife.BindView;",
        "class Test<T extends TextView> extends Activity {",
        "    @BindView(1) T thing;",
        "}"
    ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.widget.TextView;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.thing = finder.findRequiredViewAsType(source, 1, \"field 'thing'\", TextView.class);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void oneFindPerId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "import butterknife.OnClick;",
        "public class Test extends Activity {",
        "  @BindView(1) View thing1;",
        "  @OnClick(1) void doStuff() {}",
        "}"
    ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import android.view.View;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.DebouncingOnClickListener;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  private View view1;\n"
        + "  public Test_ViewBinding(final T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    View view;\n"
        + "    view = finder.findRequiredView(source, 1, \"field 'thing1' and method 'doStuff'\");\n"
        + "    target.thing1 = view;\n"
        + "    view1 = view;\n"
        + "    view.setOnClickListener(new DebouncingOnClickListener() {\n"
        + "      @Override\n"
        + "      public void doClick(View p0) {\n"
        + "        target.doStuff();\n"
        + "      }\n"
        + "    });\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.thing1 = null;\n"
        + "    view1.setOnClickListener(null);\n"
        + "    view1 = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @BindView(1) public View thing1;",
        "  @BindView(2) View thing2;",
        "  @BindView(3) protected View thing3;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError();
  }

  @Test public void nullable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @interface Nullable {}",
        "  @Nullable @BindView(1) View view;",
        "}"
    ));

    JavaFileObject binderSource = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject bindingSource = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.view = finder.findOptionalView(source, 1);\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.view = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binderSource, bindingSource);
  }

  @Test public void superclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @BindView(1) View view;",
        "}",
        "class TestOne extends Test {",
        "  @BindView(1) View thing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject binder1Source = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.view = finder.findRequiredView(source, 1, \"field 'view'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.view = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binder2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinder<T extends TestOne> extends Test_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new TestOne_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinding<T extends TestOne> extends Test_ViewBinding<T> {\n"
        + "  public TestOne_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    super.unbind();\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binder1Source, binding1Source, binder2Source, binding2Source);
  }

  @Test public void genericSuperclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test<T> extends Activity {",
        "  @BindView(1) View view;",
        "}",
        "class TestOne extends Test<String> {",
        "  @BindView(1) View thing;",
        "}",
        "class TestTwo extends Test<Object> {",
        "}"
    ));

    JavaFileObject binder1Source = JavaFileObjects.forSourceString("test/Test_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new Test_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding1Source = JavaFileObjects.forSourceString("test/Test_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.IllegalStateException;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class Test_ViewBinding<T extends Test> implements Unbinder {\n"
        + "  protected T target;\n"
        + "  public Test_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    this.target = target;\n"
        + "    target.view = finder.findRequiredView(source, 1, \"field 'view'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    if (target == null) throw new IllegalStateException(\"Bindings already cleared.\");\n"
        + "    target.view = null;\n"
        + "    this.target = null;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binder2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinder", ""
        + "package test;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinder<T extends TestOne> extends Test_ViewBinder<T> {\n"
        + "  @Override\n"
        + "  public Unbinder bind(Finder finder, T target, Object source) {\n"
        + "    return new TestOne_ViewBinding<>(target, finder, source);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject binding2Source = JavaFileObjects.forSourceString("test/TestOne_ViewBinding", ""
        + "package test;\n"
        + "import butterknife.internal.Finder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "public class TestOne_ViewBinding<T extends TestOne> extends Test_ViewBinding<T> {\n"
        + "  public TestOne_ViewBinding(T target, Finder finder, Object source) {\n"
        + "    super(target, finder, source);\n"
        + "    target.thing = finder.findRequiredView(source, 1, \"field 'thing'\");\n"
        + "  }\n"
        + "  @Override\n"
        + "  public void unbind() {\n"
        + "    T target = this.target;\n"
        + "    super.unbind();\n"
        + "    target.thing = null;\n"
        + "  }\n"
        + "}"
    );

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(binder1Source, binding1Source, binder2Source, binding2Source);
  }

  @Test public void failsInJavaPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package java.test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test {",
        "  @BindView(1) View thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView-annotated class incorrectly in Java framework package. (java.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsInAndroidPackage() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package android.test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test {",
        "  @BindView(1) View thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView-annotated class incorrectly in Android framework package. (android.test.Test)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test {",
        "  private static class Inner {",
        "    @BindView(1) View thing;",
        "  }",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields may not be contained in private classes. (test.Test.Inner.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "  @BindView(1) String thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must extend from View or be an interface. (test.Test.thing)")
        .in(source).onLine(5);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public interface Test {",
        "    @BindView(1) View thing = null;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "@BindView fields may only be contained in classes. (test.Test.thing)")
        .in(source).onLine(4);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) private View thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) static View thing;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining("@BindView fields must not be private or static. (test.Test.thing)")
        .in(source).onLine(6);
  }

  @Test public void duplicateBindingFails() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.BindView;",
        "public class Test extends Activity {",
        "    @BindView(1) View thing1;",
        "    @BindView(1) View thing2;",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            "Attempt to use @BindView for an already bound ID 1 on 'thing1'. (test.Test.thing2)")
        .in(source).onLine(7);
  }

  @Test public void failsRootViewBindingWithBadTarget() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterknife.OnItemClick;",
            "public class Test extends View {",
            "  @OnItemClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    assertAbout(javaSource())
        .that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining((
            "@OnItemClick annotation without an ID may only be used with an object of type "
                + "\"android.widget.AdapterView<?>\" or an interface. (test.Test.doStuff)"))
        .in(source)
        .onLine(6);
  }

  @Test public void failsOptionalRootViewBinding() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.Context;",
            "import android.view.View;",
            "import butterknife.OnClick;",
            "import butterknife.Optional;",
            "public class Test extends View {",
            "  @Optional @OnClick void doStuff() {}",
            "  public Test(Context context) {",
            "    super(context);",
            "  }",
            "}"));

    assertAbout(javaSource())
        .that(source)
        .processedWith(new ButterKnifeProcessor())
        .failsToCompile()
        .withErrorContaining(
            ("ID-free binding must not be annotated with @Optional. (test.Test.doStuff)"))
        .in(source)
        .onLine(7);
  }
}
