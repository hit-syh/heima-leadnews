package heima.model;

import com.heima.model.common.constants.ScheduleConstants;
import com.heima.model.common.enums.TaskTypeEnum;
import org.junit.Test;


public class test {
    @Test
    public void testModelEnum()
    {
        System.out.println(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        System.out.println(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
    }

}
