package com.xinyuan.repository;

import com.xinyuan.entity.UploadInfo;
import org.springframework.stereotype.Repository;

/**
 * @author liang
 */
@Repository
public interface UploadRepository extends BaseJpaRepository<UploadInfo, Long> {


}
