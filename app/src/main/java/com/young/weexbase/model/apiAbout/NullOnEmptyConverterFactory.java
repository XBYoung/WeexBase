package com.young.weexbase.model.apiAbout;

import com.young.weexbase.model.NoBodyEntity;

import org.xutils.common.util.LogUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2018/3/30.
 */

public class NullOnEmptyConverterFactory extends Converter.Factory {

    //创建retrofit的caller,必须指定一个返回类型
    // Call<NoBodyEntity> caller=service.noBodyRequest();
    //Converter类中的处理方式
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //匹配response对象需要返回的类型是否与我们想自定义处理的类型一致
      /*  if (NoBodyEntity.class.equals(type)) {
            return new Converter<ResponseBody, NoBodyEntity>() {
                @Override
                public NoBodyEntity convert(ResponseBody value) throws IOException {
                    //这里返回null是因为本来就是无响应实体
                    //所以也不会使用到该类的实例.
                    return null;
                }
            };
        }
           return null;
           */
        if (NoBodyEntity.class.equals(type)) {
            return (Converter<ResponseBody, NoBodyEntity>) value -> {
                //这里返回null是因为本来就是无响应实体
                //所以也不会使用到该类的实例.
                LogUtil.d(" TAG contentLength = "+value.contentLength());
                return new NoBodyEntity();
            };
        } else {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return (Converter<ResponseBody, Object>) body -> {
                if (body.contentLength() == 0) {
                    return null;
                }
                return delegate.convert(body);
            };
        }

    }


}