package com.example.daytoday;

import androidx.annotation.NonNull;

import com.example.daytoday.Model.Item;
import com.example.daytoday.Model.List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    String uid_test;
    List testList;

    ListOfListsActivity ls;

    @Before
    public void before() {
        ls = new ListOfListsActivity();
        testList = new List("Test List",(float)200.00,"list write test","2000/01/01",uid_test);
        uid_test = "testabc123";
    }

    @Test
    public void listInputTest(){
        boolean success = ls.setListDb(uid_test,testList);

        assert(success);
    }

}