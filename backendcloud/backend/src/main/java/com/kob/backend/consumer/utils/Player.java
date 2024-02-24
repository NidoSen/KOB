package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    private boolean check_tail_increasing(int step) { //检测当前回合蛇的长度是否增加
        return step <= 10 || step % 3 == 1;
    }

    public List<Cell> getCells() { //重建蛇
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y)); //起始位置
        for (int d: steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y)); //增加一节蛇身
            //如果蛇身没有变长，就去掉最后一个点（对应的是0位置处的Cell，下标最大的Cell始终记录舌头位置）
            if (!check_tail_increasing(++step)) {
                res.remove(0);
            }
        }
        return res;
    }

    //将steps转化为字符串，用于存储到数据库
    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d: steps) {
            res.append(d);
        }
        return res.toString();
    }
}
