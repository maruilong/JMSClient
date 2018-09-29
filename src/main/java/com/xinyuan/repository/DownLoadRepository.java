package com.xinyuan.repository;

import com.xinyuan.entity.DownloadInfo;
import org.springframework.stereotype.Repository;

/**
 * @author liang
 */
@Repository
public interface DownLoadRepository extends BaseJpaRepository<DownloadInfo, Long> {
}
