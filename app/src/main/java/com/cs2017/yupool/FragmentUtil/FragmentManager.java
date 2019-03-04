package com.cs2017.yupool.FragmentUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.cs2017.yupool.PoolList.PoolListFragment;
import com.cs2017.yupool.R;
import com.cs2017.yupool.PoolRegister.PoolRegisterFragment;
import com.cs2017.yupool.ReqRecv.ReceiveList.ReceiveListFragment;
import com.cs2017.yupool.ReqRecv.RequestList.RequestListFragment;
import com.cs2017.yupool.UI.MainActivity;
import com.cs2017.yupool.DriverRegister.RegisterFragment;

/*
    프래그먼트들간의 전환을 위한 Class
    SingleTone
 */
public class FragmentManager {

    private static FragmentManager instance = new FragmentManager(); // 싱글톤 인스턴스
    private FragmentManager(){}; // 생성자
    private Context context; // 어플리케이션 컨텍스트

    private static final String TAG = "FragmentManager";

    private int currentFragment; // 현재 프래그먼트 구분자
    public static final int POOL_LIST_FRAGMENT = 0;
    public static final int REGISTER_FRAGMENT = 1;
    public static final int REG_POOL_FRAGMENT = 2;
    public static final int REG_DETAIL_FRAGMENT = 3;
    public static final int REQ_LIST_FRAGMENT = 4;
    public static final int RECV_LIST_FRAGMENT = 5;

    private PoolRegisterFragment driverFragment;


    private RequestListFragment requestListFragment;
    private ReceiveListFragment receiveListFragment;


    // 인스턴스 반환
    public static FragmentManager getInstance(){
        return instance;
    }

    // 어플리케이션 컨텍스트 등록
    public void registerContext(Context context){
        this.context = context;

    }

    // 프래그먼트 전환
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);

        if(fragment instanceof PoolListFragment){
            currentFragment = POOL_LIST_FRAGMENT;
        }else if (fragment instanceof RegisterFragment){
            currentFragment = REGISTER_FRAGMENT;
        }else if (fragment instanceof PoolRegisterFragment){
            currentFragment = REG_POOL_FRAGMENT;
        }else if(fragment instanceof ReceiveListFragment){
            currentFragment = RECV_LIST_FRAGMENT;
        } else if (fragment instanceof RequestListFragment){
            currentFragment = REQ_LIST_FRAGMENT;
        }
        transaction.commitAllowingStateLoss();
        //transaction.commitNow();
        //transaction.commit();
    }
    // 현재 표시되고있는 fragment 반환
    public int getCurrentFragment(){
        return currentFragment;
    }



    public void setDriverFragment(PoolRegisterFragment driverFragment){
        this.driverFragment = driverFragment;
    }


    public void setRequestListFragment(RequestListFragment requestListFragment) {
        this.requestListFragment = requestListFragment;
    }

    public void setReceiveListFragment(ReceiveListFragment receiveListFragment) {
        this.receiveListFragment = receiveListFragment;
    }

    // 드라이버 메인 화면으로 프레그먼트 전환
    public void changeToDriverMain(){
        replaceFragment(driverFragment);
    }

    public void chageToRequestFragment(){
        replaceFragment(requestListFragment);
    }
    public void changeToReceiveFragment(){
        replaceFragment(receiveListFragment);
    }

}
