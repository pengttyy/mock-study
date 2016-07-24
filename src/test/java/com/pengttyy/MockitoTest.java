package com.pengttyy;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by KaiPeng on 2016-07-23,0023.
 */
public class MockitoTest {

    private LinkedList mockedList = mock(LinkedList.class);

    /**
     * 1.验证是否调用方法
     * @see //site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#1
     * @throws Exception
     */
    @Test
    public void verify_behaviour() throws Exception {
        //创建mock对象
        List mockedList = mock(List.class);

        //使用mock对象并调用
        mockedList.add("one");
        mockedList.clear();;
        //验证是否调用
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    /**
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#2
     * 2.使用桩
     * @throws Exception
     */
    @Test
    public void how_about_some_stubbing() throws Exception {
        LinkedList  mockedList = mock(LinkedList.class);
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        System.out.println(mockedList.get(0));
        verify(mockedList).get(0);
    }

    /**
     * 3.参数匹配器
     * @throws Exception
     */
    @Test
    public void argument_matchers() throws Exception {
        LinkedList  mockedList = mock(LinkedList.class);
        //输入任意int类型的值都会返回“element”
        when(mockedList.get(anyInt())).thenReturn("element");
        //自己编写参数核匹配器
        //when(mockedList.contains(argThat(isValid()))).thenReturn("element");
        System.out.println(mockedList.get(999));
        //验证
        verify(mockedList).get(anyInt());


        //verify(mockedList).add(eq("pengttyy"));       //someMethod(anyInt(), anyString(), eq("third argument"));
        //above is correct - eq() is also an argument matcher
        //verify(mockedList).someMethod(anyInt(), anyString(), "third argument");
    }

    /**
     *4.一个方法调用次数，或至少调用次数，或永远不调用
     */
    @Test
    public void verifying_exact_number_invocations_at_least_x() throws Exception {
        mockedList = mock(LinkedList.class);
        mockedList.add("once");
        verify(mockedList).add("once");
        verify(mockedList,times(1)).add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        verify(mockedList,times(2)).add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        verify(mockedList,times(3)).add("three times");

        //不调用
        verify(mockedList,never()).add("never happened");

        //至少一次
        verify(mockedList, atLeastOnce()).add("three times");
        //至少2次
        verify(mockedList, atLeast(2)).add("three times");
        //最多5次
        verify(mockedList, atMost(5)).add("three times");
    }

    /**
     * 5.无方法返回值时抛异常
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#5
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void stubing_with_exception() throws Exception {
        //这种方式适用于没有返回值的方法抛异常
        doThrow(new RuntimeException()).when(mockedList).clear();
        mockedList.clear();
    }

    /**
     * 6.验证多个对象调用顺序
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#6
     * @throws Exception
     */
    @Test
    public void verification_in_order() throws Exception {
        // A. 单一对象方法调用的顺序
        List singleMock = mock(List.class);

        //using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        //创建顺序对象
        InOrder inOrder = inOrder(singleMock);

        //following will make sure that add is first called with "was added first, then with "was added second"
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        // B. Multiple mocks that must be used in a particular order
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called three");
        firstMock.add("was called two");
        secondMock.add("was called four");

        //create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder2 = inOrder(firstMock, secondMock);

        //following will make sure that firstMock was called before secondMock
        inOrder2.verify(firstMock).add("was called first");
        inOrder2.verify(secondMock).add("was called three");
        inOrder2.verify(firstMock).add("was called two");
        inOrder2.verify(secondMock).add("was called four");
    }

    /**
     * 7.验证没有交互，或者对象没有被调用
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#7
     * @throws Exception
     */
    @Test
    public void verify_not_interacted() throws Exception {
        List mockTwo = mock(List.class);
        List mockThree = mock(List.class);

        //mockTwo.add("xxx");
        //验证对象没有被调用,或者说没有交互
        verifyZeroInteractions(mockTwo, mockThree);
    }

    /**
     * 8.发现多余的交互
     * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#8
     * @throws Exception
     */
    @Test
    public void find_redundant_invocations() throws Exception {
        //using mocks
        mockedList.add("one");

        //只要验证add("one"),那么没有验证的add("two")就是多余调用
        //mockedList.add("two");

        verify(mockedList).add("one");

        //following verification will fail
        verifyNoMoreInteractions(mockedList);
    }

    /**
     * 9.使用@Mock注解
     * @throws Exception
     */
    @Test
    public void name() throws Exception {

    }
}
