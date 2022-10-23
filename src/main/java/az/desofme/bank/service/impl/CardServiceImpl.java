package az.desofme.bank.service.impl;

import az.desofme.bank.dto.request.CardToCardRequest;
import az.desofme.bank.dto.response.CreateCustomerResponse;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.entity.Card;
import az.desofme.bank.entity.Customer;
import az.desofme.bank.exceptions.BankException;
import az.desofme.bank.jwt.JwtService;
import az.desofme.bank.repository.AccountRepository;
import az.desofme.bank.repository.CardRepository;
import az.desofme.bank.service.AccountService;
import az.desofme.bank.service.CardService;
import az.desofme.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;
    private static final Long THREE_YEARS = (long) (3 * 365 * 24 * 60 * 60);
    @Override
    @Transactional
    public ResponseModel<Object> orderCard() {
        try{
            var pin = jwtService.getPinFromRequest(request);
            var customer = customerService.getByPin(pin);
            var account = accountService.createAccount(customer);
            var card = new Card();
            card.setAccount(account);
            card.setCvv(getRandomCvv());
            card.setPvv(0);
            card.setCardNumber(generateRandomCardNumber());
            card.setExpiredAt(Date.from(Instant.now().plusSeconds(THREE_YEARS)));
            card.setCreatedAt(new Date());

            var savedCard = cardRepository.save(card);

            sendCardDetails(savedCard);

            return ResponseModel.builder()
                    .message(HttpStatus.OK.toString())
                    .code(HttpStatus.OK.name())
                    .build();

        }catch (BankException ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<Object>builder()
                    .data(null)
                    .code(ex.getCode())
                    .message(ex.getMessage())
                    .build();
            return responseModel;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.<Object>builder()
                    .data(null)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .message(ex.getLocalizedMessage())
                    .build();
            return responseModel;
        }
    }

    @Override
    @Transactional
    public ResponseModel<Object> cardToCard(CardToCardRequest cardToCardRequest) {
        try{

            var senderAccount = accountService.getById(cardToCardRequest.getAccountId());
            if(cardToCardRequest.getAmount().compareTo(senderAccount.getBalance()) == 1){
                throw new BankException(
                        "Insufficient balance, balance is:" + senderAccount.getBalance(),
                        HttpStatus.BAD_REQUEST.name()
                );
            }
            var receiverAccount = getByCardNumber(cardToCardRequest.getCardNumber()).getAccount();
            receiverAccount.setBalance(receiverAccount.getBalance().add(cardToCardRequest.getAmount()));
            accountRepository.save(receiverAccount);
            senderAccount.setBalance(senderAccount.getBalance().subtract(cardToCardRequest.getAmount()));
            accountRepository.save(senderAccount);

            return ResponseModel.builder()
                    .message(HttpStatus.OK.toString())
                    .code(HttpStatus.OK.name())
                    .build();

        }catch (BankException ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.builder()
                    .data(null)
                    .error(true)
                    .code(ex.getCode())
                    .message(ex.getMessage())
                    .build();
            return responseModel;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            var responseModel = ResponseModel.builder()
                    .data(null)
                    .error(true)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    .message(ex.getLocalizedMessage())
                    .build();
            return responseModel;
        }
    }

    public Card getByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(()->new BankException(
                        "Card not found with card number:" + cardNumber,
                        HttpStatus.BAD_REQUEST.name()
                ));
    }

    private Integer getRandomCvv() {
        var random = new Random();
        return random.nextInt(1000);
    }

    private String generateRandomCardNumber() {
        var random = new Random();
        String cardNumber = "4169";
        for (int i = 0; i < 12; i++) {
            cardNumber += random.nextInt(10);
        }
        return cardNumber;
    }

    @Async
    protected void sendCardDetails(Card card) throws Exception{
        String text = "<p>Card holder: " + card.getAccount().getCustomer().getName() + " " + card.getAccount().getCustomer().getSurname() + "</p></br>" +
                "<p>Card number: " + card.getCardNumber() + "</p></br>" +
                "<p>Expired Date: " + getExpiredDateAsString(card.getExpiredAt()) + "</p></br>" +
                "<p>CVV: " + card.getCvv() + "</p><br>";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setText(text, true);
        helper.setSubject("Card details");
        helper.setFrom(new InternetAddress("vusalr06@gmail.com", "Desofme Bank"));
        helper.setTo(card.getAccount().getCustomer().getEmail());
        javaMailSender.send(message);
    }

    private String getExpiredDateAsString(Date expiredDate) {
        var month = expiredDate.getMonth() < 9 ? "0" + expiredDate.getMonth() + 1 : String.valueOf(expiredDate.getMonth() + 1);
        var year = expiredDate.getYear() + 1900;
        return month + "/" + year;
    }

}
