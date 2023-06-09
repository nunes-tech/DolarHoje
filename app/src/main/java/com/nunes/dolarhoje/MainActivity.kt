package com.nunes.dolarhoje

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuProvider
import com.nunes.dolarhoje.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }
    private var dolarHoje:Double? = null

    var job:Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        iniciarMenu()

       job = CoroutineScope(Dispatchers.IO).launch {
           dolarHoje = webScraping()
        }

        binding.editTextDolar.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (dolarHoje != null) {
                        if (s != null) {
                            if (s.isNotEmpty()){
                                if (s.startsWith(".") || s.startsWith(",")) return
                                val qtdDolarDigitado = s.toString().toDouble()
                                val resultado = dolarHoje!! * qtdDolarDigitado
                                val resultadoFormatado = String.format("%.2f", resultado)
                                binding.editTextReal.text.clear()
                                binding.editTextReal.hint = "R$ " + resultadoFormatado
                            } else {
                                binding.editTextReal.hint = "R$ " + dolarHoje.toString()
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            }
        )


        binding.editTextReal.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                    if (dolarHoje != null) {
                       val valoReal = 1.0 / dolarHoje!!
                        if (s != null) {
                            if (s.isNotEmpty()){
                                if (s.startsWith(".") || s.startsWith(",")) return
                                val qtdRealDigitado = s.toString().toDouble()
                                val resultado = valoReal!! * qtdRealDigitado
                                val resultadoFormatado = String.format("%.2f", resultado)
                                binding.editTextDolar.text.clear()
                                binding.editTextDolar.hint = "USD " + resultadoFormatado
                            } else {
                                binding.editTextDolar.hint = "USD 1.0"
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            }
        )

    }

    private fun iniciarMenu() {
      addMenuProvider(
          object :MenuProvider{
              override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                  menuInflater.inflate(R.menu.menu_principal, menu)
              }

              override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                  if (menuItem.itemId == R.id.sobre) {
                      val url = "https://github.com/nunes-tech/DolarHoje"
                      val intent = Intent( Intent.ACTION_VIEW, Uri.parse(url))
                      startActivity(intent)
                  }
                  return true
              }

          }
      )
    }

    private suspend fun webScraping(): Double {
        val url = "https://dolarhoje.com/"

        val document = Jsoup.connect( url ).get()

        val valorDolar = document.select("input#nacional").attr("value")
        val dolarDouble = valorDolar.replace(",", ".").toDouble()
        binding.editTextReal.hint = "R$ " + dolarDouble.toString()

        return  dolarDouble
    }



    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}