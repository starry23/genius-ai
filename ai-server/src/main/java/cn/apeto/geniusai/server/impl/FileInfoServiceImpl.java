package cn.apeto.geniusai.server.impl;

import cn.apeto.geniusai.server.entity.FileInfo;
import cn.apeto.geniusai.server.mapper.FileInfoMapper;
import cn.apeto.geniusai.server.service.FileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 附件信息 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2024-01-30 04:09:53
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

}
