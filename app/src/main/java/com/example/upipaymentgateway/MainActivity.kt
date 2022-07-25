package com.example.upipaymentgateway

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.upipaymentgateway.ui.theme.*
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), PaymentStatusListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UPIPaymentGatewayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(backgroundColor = greenColor,
                                title = {
                                    Text(
                                        text = "UPI Payments in Android",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                })
                        }) {
                        upiPayments(this)
                    }
                }
            }
        }
    }
    override fun onTransactionCancelled() {
        Toast.makeText(this, "Transaction canceled by user..", Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        Toast.makeText(this, "Transaction completed by user..", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun upiPayments(mainActivity: MainActivity) {
    val ctx = LocalContext.current
    val activity = (LocalContext.current as? Activity)

    val amount = remember {
        mutableStateOf(TextFieldValue())
    }
    val upiId = remember {
        mutableStateOf(TextFieldValue())
    }
    val name = remember {
        mutableStateOf(TextFieldValue())
    }
    val description = remember {
        mutableStateOf(TextFieldValue())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "UPI Payments in Android",
            color = greenColor,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            placeholder = { Text(text = "Enter amount to be paid") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = upiId.value,

            onValueChange = { upiId.value = it },

            placeholder = { Text(text = "Enter UPI Id") },

            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = name.value,

            onValueChange = { name.value = it },

            placeholder = { Text(text = "Enter name") },

            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

            singleLine = true,
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            placeholder = { Text(text = "Enter Description") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val c: Date = Calendar.getInstance().getTime()
                val df = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())
                val transcId: String = df.format(c)
                makePayment(
                    amount.value.text,
                    upiId.value.text,
                    name.value.text,
                    description.value.text,
                    transcId,
                    ctx,
                    activity!!,
                    mainActivity
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Make Payment", modifier = Modifier.padding(8.dp))
        }
    }
}


private fun makePayment(
    amount: String,
    upi: String,
    name: String,
    desc: String,
    transcId: String, ctx: Context, activity: Activity, mainActivity: PaymentStatusListener
) {
    try {
        val easyUpiPayment = EasyUpiPayment(activity) {
            this.paymentApp = PaymentApp.ALL
            this.payeeVpa = upi
            this.payeeName = name
            this.transactionId = transcId
            this.transactionRefId = transcId
            this.payeeMerchantCode = transcId
            this.description = desc
            this.amount = amount
        }
        easyUpiPayment.setPaymentStatusListener(mainActivity)
        easyUpiPayment.startPayment()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(ctx, e.message, Toast.LENGTH_SHORT).show()
    }
}
