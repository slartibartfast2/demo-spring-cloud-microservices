package ea.slartibartfast.payment.controller;

import ea.slartibartfast.payment.controller.request.CreatePaymentRequest;
import ea.slartibartfast.payment.controller.response.CreatePaymentResponse;
import ea.slartibartfast.payment.controller.response.RetrievePaymentResponse;
import ea.slartibartfast.payment.controller.response.RetrievePaymentResponseWithCard;
import ea.slartibartfast.payment.manager.PaymentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentManager paymentManager;

    @PostMapping(produces = "application/json")
    public CreatePaymentResponse add(@Valid @RequestBody CreatePaymentRequest createPaymentRequest) {
        return paymentManager.createPayment(createPaymentRequest);
    }

    @GetMapping
    public List<RetrievePaymentResponse> retrieveAllPayments() {
        return paymentManager.retrieveAllPayments();
    }

    @GetMapping("/{id}")
    public RetrievePaymentResponse retrievePaymentById(@PathVariable("id") Long id) {
        return paymentManager.retrievePayment(id);
    }

    @GetMapping("/{id}/with-card")
    public RetrievePaymentResponseWithCard retrievePaymentWithCard(@PathVariable("id") Long id,
                                                                   @RequestParam(value = "delay", required = false, defaultValue = "0")  int delay,
                                                                   @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return paymentManager.retrievePaymentWithCard(id, delay, faultPercent);
    }
}
