package az.desofme.bank.schedul;

import az.desofme.bank.repository.ConfirmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

import static az.desofme.bank.constants.Times.SCHEDULE_DELETE_TOKEN_TIME;

@Component
@RequiredArgsConstructor
public class ConfirmTokenSchedule {

    private final ConfirmTokenRepository confirmTokenRepository;

    @Scheduled(fixedDelay = SCHEDULE_DELETE_TOKEN_TIME)
    public void deleteExpiredTokens() {
        var tokenList = confirmTokenRepository.findAll();

        tokenList.stream().forEach(token -> {
                    if (token.getExpiredAt().before(new Date())) {
                        confirmTokenRepository.delete(token);
                    }
                }
        );
    }

}
