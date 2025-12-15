package com.secondhand.web.controller.system;

import com.secondhand.common.core.domain.R;
import com.secondhand.system.domain.SysNotice;
import com.secondhand.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client/message")
public class UserMessageController {

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @GetMapping("/list")
    public R<List<SysNotice>> list()
    {
        SysNotice notice = SysNotice.builder().status("0").build();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return R.ok(list);
    }

    /**
     * 获取通知列表
     */
    @GetMapping("/noticelist")
    public R<List<SysNotice>> noticelist()
    {
        SysNotice notice = SysNotice.builder().status("0").noticeType("1").build();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return R.ok(list);
    }

    /**
     * 获取公告列表
     */
    @GetMapping("/announcementlist")
    public R<List<SysNotice>> announcementlist()
    {
        SysNotice notice = SysNotice.builder().status("0").noticeType("2").build();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return R.ok(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @GetMapping(value = "/{noticeId}")
    public R<SysNotice> getInfo(@PathVariable Long noticeId)
    {
        return R.ok(noticeService.selectNoticeById(noticeId));
    }

}
