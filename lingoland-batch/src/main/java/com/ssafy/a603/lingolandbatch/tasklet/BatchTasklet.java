package com.ssafy.a603.lingolandbatch.tasklet;

import com.ssafy.a603.lingolandbatch.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchTasklet implements Tasklet {

    private final ProblemService problemService;
    @Value("${spring.taskletCnt}")
    private int taskletCnt;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("tasklet start");
        for(int i = 0; i < taskletCnt; i++){
            log.info("tasklet num : {}", i);
            problemService.makeProblem();
//            problemService.makeProblemRestTemplate();
//            problemService.makeProblemVirtualThread();
        }
        log.info("tasklet end");
        return RepeatStatus.FINISHED;
    }
}
