package com.pengttyy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Created by KaiPeng on 2016-07-23,0023.
 */
@RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationTest {
    @Mock
    private List list;

    @Before
    public void setUp() throws Exception {
        //使用annotation的第一种方式
        //第二种使用junit运行器MockitoAnnotations
        //MockitoAnnotations.initMocks(this);
    }

    /**
     * 9.使用annotation注解模拟对象
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#mock_annotation
     *
     * @throws Exception
     */
    @Test
    public void mock_annotation() throws Exception {
        list.add("123");
        verify(list).add("123");
    }

    /**
     * 10.一个对象方法的多次调用
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#10
     *
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void consecutive_calls() throws Exception {
        when(list.get(1))
                .thenReturn("foo")
                .thenThrow(new RuntimeException());

        //First call: throws runtime exception:
        //list.get(1);

        //第一次调用返回foo
        assertEquals("foo", list.get(1));

        //第二次调用异常
        list.get(1);
    }

    /**
     * 11.预期返回值的回调接口实现复杂返回值
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#11
     *
     * @throws Exception
     */
    @Test
    public void stubbing_with_callbacks() throws Exception {
        when(list.get(anyInt())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Method method = invocation.getMethod();
                System.out.println(method.toString());
                Object mock = invocation.getMock();
                return "called with arguments: " + args;
            }
        });
        System.out.println(list.get(213));
    }

    /**
     * 12.doXXX()方法
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#11
     *
     * @throws Exception
     */
    @Test
    public void family_of_methods() throws Exception {
        //doXXX()与when()应该是两种不同的方式
        doReturn("xxx").when(list).get(1);
        when(list.get(2)).thenReturn("yyy");

//        doReturn(Object)
//
//        doThrow(Throwable...)
//
//        doThrow(Class)
//
//        doAnswer(Answer)
//
//        doNothing()
//
//        doCallRealMethod()

        System.out.println(list.get(1));
        System.out.println(list.get(2));


    }

    /**
     * 13.1将真实对象替换
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#13
     *
     * @throws Exception
     */
    @Test
    public void Spying_on_real_objects() throws Exception {
        List list = new LinkedList();
        List spy = spy(list);

        //optionally, you can stub out some methods:
        when(spy.size()).thenReturn(100);

        //去调用真实的方法
        spy.add("one");
        spy.add("two");

        //prints "one" - the first element of a list
        System.out.println(spy.get(0));

        //size() method was stubbed - 100 is printed
        System.out.println(spy.size());

        //optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");
    }

    /**
     * 13.2
     * 这里反映了调用doXXX方法与when()方法的一个差异
     * 使用真对象spy不能是final方法
     *
     * @throws Exception
     */
    @Test
    public void spying_on_real_objects2() throws Exception {
        List list = new LinkedList();
        List spy = spy(list);

        //Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
        //when会首先去调用真实的方法
        //when(spy.get(0)).thenReturn("foo");

        //You have to use doReturn() for stubbing
        doReturn("foo").when(spy).get(0);
    }

    /**
     * 14.改变桩的默认值，不报NPE，而返回SmartNull
     *
     * @throws Exception
     */
    @Test
    public void change_default_value_of_unstubbed() throws Exception {
        List mock = mock(List.class, Mockito.RETURNS_SMART_NULLS);
        mock.add("123");
        System.out.println(mock.get(0));
    }

    /**
     * 15.参数捕获器，验证参数是否符合规范
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#13
     *
     * @throws Exception
     */
    @Test
    public void capturing_arg_for_further_assertions() throws Exception {
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        list.add("123");
        verify(list).add(argument.capture());
        assertEquals("123", argument.getValue());
    }

    /**
     * 16.调用部分方法的真实实现
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#16
     *
     * @throws Exception
     */
    @Test
    public void peal_partial_mocks() throws Exception {
        //you can create partial mock with spy() method:
        LinkedList list = spy(new LinkedList());
        doCallRealMethod().when(list).add("123");//部分调用真实的对象
        list.add("123");
        System.out.println(list.size());
    }

    /**
     * 17.重置模拟对象
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#17
     * 一般不需要重置，可能意味着测试问题，或测试的内容太多，可以拆分成小测试
     *
     * @throws Exception
     */
    @Test
    public void reset_mock() throws Exception {
        List mock = mock(List.class);
        when(mock.size()).thenReturn(10);
        mock.add(1);

        reset(mock);
    }

    /**
     * 19.行为测试驱动
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#19
     *
     * @throws Exception
     */
    @Test
    public void bdd_test() throws Exception {
        // TODO: 2016-07-24,0024 行为驱动开发
    }

    /**
     * 20可序列化的mock
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#20
     *
     * @throws Exception
     */
    @Test
    public void serrializable_mocks() throws Exception {
        List<Object> list = new ArrayList<Object>();
        List<Object> spy = mock(ArrayList.class, withSettings()
                .spiedInstance(list)
                .defaultAnswer(CALLS_REAL_METHODS)
                .serializable());
    }

    /**
     * 21.新的注解
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#21
     *
     * @throws Exception
     */
    @Test
    public void new_annotations() throws Exception {
        //@Captor 参数捕获器
        //@Spy 监视器对象
        //@InjectMocks 是将用@Mock标记的对象注入到被测试对象中
    }

    /**
     * 22.验证是否超时
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#22
     *
     * @throws Exception
     */
    @Test
    public void verification_with_timeout() throws Exception {

        //passes when someMethod() is called within given time span
        //verify(list, timeout(100)).someMethod();
        //above is an alias to:
        //verify(list, timeout(100).times(1)).someMethod();

        //passes when someMethod() is called *exactly* 2 times within given time span
        //verify(list, timeout(100).times(2)).someMethod();

        //passes when someMethod() is called *at least* 2 times within given time span
        //verify(list, timeout(100).atLeast(2)).someMethod();

        //verifies someMethod() within given time span using given verification mode
        //useful only if you have your own custom verification modes.
        //verify(list, new Timeout(100, yourOwnVerificationMode)).someMethod();
    }

    /**
     * 23.自动实例化
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#23
     * @throws Exception
     */
    @Test
    public void automatic_instantiation() throws Exception {
        //instead:
//        @Spy BeerDrinker drinker = new BeerDrinker();
        //you can write:
//        @Spy BeerDrinker drinker;

        //same applies to @InjectMocks annotation:
//        @InjectMocks LocalPub;
    }

    /**
     * 24.一行存根
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#24
     * @throws Exception
     */
    @Test
    public void one_liner_stubs() throws Exception {
        List aReturn = when(mock(List.class).get(0)).thenReturn("return").getMock();
        System.out.println(aReturn.get(0));
    }

    /**
     * 25.忽略其它存根
     * @throws Exception
     */
    @Test
    public void ignore_stubs() throws Exception {
//        verify(mock).foo();
//        verify(mockTwo).bar();
//
//        //ignores all stubbed methods:
//        verifyNoMoreInteractions(ignoreStubs(mock, mockTwo));
//
//        //creates InOrder that will ignore stubbed
//        InOrder inOrder = inOrder(ignoreStubs(mock, mockTwo));
//        inOrder.verify(mock).foo();
//        inOrder.verify(mockTwo).bar();
//        inOrder.verifyNoMoreInteractions();
    }

    /**
     * 26,验证是否是一个mock对象或spy对象
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#26
     * @throws Exception
     */
    @Test
    public void mock_details() throws Exception {
        List list = new ArrayList();
        assertFalse(mockingDetails(list).isMock());
        assertFalse(mockingDetails(list).isSpy());
    }
}
