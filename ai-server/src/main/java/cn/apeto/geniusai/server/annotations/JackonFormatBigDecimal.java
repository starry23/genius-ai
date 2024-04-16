package cn.apeto.geniusai.server.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cn.apeto.geniusai.server.serializer.JackonBigDecimalSerializer;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = JackonBigDecimalSerializer.class)
public @interface JackonFormatBigDecimal {
}
