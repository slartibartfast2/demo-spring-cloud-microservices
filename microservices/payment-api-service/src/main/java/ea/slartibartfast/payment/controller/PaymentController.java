package ea.slartibartfast.payment.controller;

import ea.slartibartfast.payment.controller.request.CreatePaymentRequest;
import ea.slartibartfast.payment.controller.response.CreatePaymentResponse;
import ea.slartibartfast.payment.controller.response.RetrievePaymentResponse;
import ea.slartibartfast.payment.controller.response.RetrievePaymentResponseWithCard;
import ea.slartibartfast.payment.manager.PaymentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public RetrievePaymentResponseWithCard retrievePaymentWithCard(@PathVariable("id") Long id) {
        return paymentManager.retrievePaymentWithCard(id);
    }
}
