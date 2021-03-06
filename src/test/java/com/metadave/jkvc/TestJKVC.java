/*
 * -------------------------------------------------------------------
 *
 *   Copyright (c) 2013 Dave Parfitt
 *
 *   This file is provided to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License.  You may obtain
 *   a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 * -------------------------------------------------------------------
 */
package com.metadave.jkvc;


import com.metadave.jkvc.eval.JKVCPathEvaluator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestJKVC {
    @Test
    public void testSimple() throws Exception {

        List la = new ArrayList<String>();
        la.add("ListFoo1");
        la.add("ListBar1");
        la.add("ListBaz1");

        List lb = new ArrayList<String>();
        lb.add("3");
        lb.add("5");

        Map<String, List<String>> inner = new HashMap<String, List<String>>();
        inner.put("listA", la);
        inner.put("listB", lb);

        Map<String, Map<String, List<String>>> hm = new HashMap<String, Map<String, List<String>>>();
        hm.put("foo", inner);

        //System.out.println("Test data = " + hm);

        // TODO: NO NEGATIVE TEST CASES ATM, THINK ABOUT HOW TO HANDLE ERRORS

        {
            HashMap<String, List<Integer>> m = new HashMap<String, List<Integer>>();
            List<Integer> a = new ArrayList<Integer>();
            a.add(100);
            a.add(200);
            a.add(300);

            List<Integer> b = new ArrayList<Integer>();
            b.add(111);
            b.add(222);
            b.add(333);

            m.put("foo", a);
            m.put("bar", b);

            assertEquals("[100, 200, 300]", JKVC.prepare("foo").eval(m).toString());
            assertEquals(200, JKVC.prepare("foo.@i[1]").eval(m));
            assertEquals(3l, JKVC.prepare("foo.@count").eval(m));
            assertEquals(3, JKVC.prepare("foo.#size").eval(m));
            assertEquals(600.0d, JKVC.prepare("foo.@sum").eval(m));
            assertEquals(111, JKVC.prepare("bar.@i[0]").eval(m));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listA.@i[0:2]");
            String expected[] = {"ListFoo1", "ListBar1", "ListBaz1"};
            assertArrayEquals(expected, ((List)e.eval(hm)).toArray());
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listA.@i[0:1].@count");
            assertEquals(2l, e.eval(hm));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listA.@count");
            assertEquals(3l,e.eval(hm));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listB.@count");
            assertEquals(2l,e.eval(hm));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listB.@sum");
            assertEquals(8.0d,e.eval(hm));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listB.@avg");
            assertEquals(4.0d,e.eval(hm));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.listB.#size");
            assertEquals(2,e.eval(hm));
        }

        {
            JKVCPathEvaluator e = JKVC.prepare("foo.#getClass.#getName");
            assertEquals("java.util.HashMap", e.eval(hm));
        }


    }
}
