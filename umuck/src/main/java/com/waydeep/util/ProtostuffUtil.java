package com.waydeep.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.waydeep.umuck.model.HttpResult;
import com.waydeep.umuck.model.TestBean;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 序列化工具Protostuff
 */
public class ProtostuffUtil {

    private static ThreadLocal<HashMap<String,Schema<?>>> threadLocal = new ThreadLocal<HashMap<String,Schema<?>>>();
    private static HashMap<String,Schema<?>> mStuffMap = new HashMap<String,Schema<?>>();
    public final static int mByteBaseSize = 1024 * 4;
    /**
     * 静态空Byte数组
     */
    public final static byte[] mEmptyBytes = new byte[0];

    public  ProtostuffUtil() {
        threadLocal.set(mStuffMap);
    }

    /**
     * Protostuff序列化对象
     * @param obj
     * @param cls
     * @return
     */
    public static byte[] encodeTuff(Object obj, Class<?> cls) {
        long start = System.currentTimeMillis() ;
        byte[] mBytes = mEmptyBytes;
        if(obj == null){
            return mBytes;
        }
        Schema<Object> schema = (Schema<Object>) getClassSchema(cls);
        LinkedBuffer buffer = LinkedBuffer.allocate(mByteBaseSize);
        try {
            mBytes = ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            buffer.clear();
        }
        long end = System.currentTimeMillis() ;
        long  userTime = end - start;
        System.out.println("耗时："+userTime);
        return mBytes;
    }


    /**
     * 反序列化函数.ProtoStuff格式
     * @param data
     * @param cls
     * @return
     */
    public static Object decodeBuf(byte[] data,Class<?> cls){
        long start = System.currentTimeMillis() ;
        Object obj=null;
        if(data==null || data.length==0){
            return obj;
        }
        try {
            Schema<Object> mSchema = (Schema<Object>) getClassSchema(cls);
            obj=cls.newInstance();
            ProtostuffIOUtil.mergeFrom(data,obj , mSchema);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        finally{
            data = mEmptyBytes;
        }
        long end = System.currentTimeMillis() ;
        long  userTime = end - start;
        System.out.println("反序列化耗时："+userTime);
        return obj;
    }

    /**
     * 得到Schema
     * @param mClass
     * @return
     */
    public static Schema<?> getClassSchema(Class<?> mClass){
        String clsName = mClass.getName();
        HashMap<String,Schema<?>> map = threadLocal.get();
        Schema<?> schema = null;
        if(map != null){
            boolean mKeyState =  map.containsKey(clsName);
            if(mKeyState==false){
                schema = RuntimeSchema.getSchema(mClass);
                map.put(clsName,schema);
            }else{
                schema = map.get(clsName);
            }
        }else{
            boolean mKeyState =  mStuffMap.containsKey(clsName);
            if(mKeyState==false){
                schema = RuntimeSchema.getSchema(mClass);
                mStuffMap.put(clsName,schema);
            }else{
                schema = mStuffMap.get(clsName);
            }
        }
        return schema;
    }
    @Test
    public void test(){

        final List<TestBean> list = new ArrayList<>();

        for(int i=0;i<10000;i++){
            TestBean testBean = new TestBean();
            testBean.setmInt(i);
            testBean.setmStr("pojo"+i);
            list.add(testBean);
        }
         final HttpResult httpResult = HttpResult.newResult(true,list,null);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<2;i++){
            executorService.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis() ;
                    try {
                        byte[] mBytes  = ProtostuffUtil.encodeTuff(httpResult,httpResult.getClass());
                        Object object = ProtostuffUtil.decodeBuf(mBytes,HttpResult.class);
                        HttpResult testBean2 = (HttpResult)object;
                        List<TestBean> list2 = (List<TestBean>) testBean2.getResult();
                        for(TestBean t:list2){
                            System.out.println("testBean2："+ t.getmStr());
                            System.out.println("testBean2："+ t.getmInt());
                        }
                    }catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    long end = System.currentTimeMillis() ;
                    long  userTime = end - start;
                    System.out.println("总耗时："+userTime);
                }
            }));
        }
        executorService.shutdown();
    }
}
