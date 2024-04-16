package cn.apeto.geniusai.server.domain.vo;

import lombok.Data;

/**
 * @author apeto
 * @create 2023/5/15 11:42
 */
@Data
public class TrendVO<T> {


  private String date;

  private T count;

}
