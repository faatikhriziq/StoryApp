package com.faatikhriziq.storyapp.ui.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.faatikhriziq.storyapp.R
import com.faatikhriziq.storyapp.data.remote.request.LoginRequest
import com.faatikhriziq.storyapp.data.remote.response.LoginResultResponse
import com.faatikhriziq.storyapp.data.source.local.entity.UserEntity
import com.faatikhriziq.storyapp.databinding.FragmentLoginBinding
import com.faatikhriziq.storyapp.helper.ViewModelFactory
import com.faatikhriziq.storyapp.data.repository.Result
@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private var backPressedTime: Long = 0
    private val BACK_INTERVAL = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        val animator = ObjectAnimator.ofFloat(binding.ilLogo, View.ALPHA, 0f, 1f)
        animator.duration = 6000
        animator.start()
    }

    private fun setupAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + BACK_INTERVAL > System.currentTimeMillis()) {
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.press_back_again_to_exit,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })

        binding.apply {
            edLoginPassword.setOnEditorActionListener { _, actionId, _ ->
                clearFocusOnDoneAction(actionId)
            }

            btnSignIn.setOnClickListener {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()

                login(email, password)
            }
            btnRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
            )
        }
    }

    private fun login(email: String, password: String) {
        binding.apply {
            when {
                email.isEmpty() -> {
                    edLoginEmail.error = R.string.please_fill_the_email.toString()
                }
                password.isEmpty() -> {
                    edLoginPassword.error = R.string.please_fill_the_password.toString()
                }
                else -> {
                    executeLogin(email, password)
                }
            }
        }
    }

    private fun executeLogin(email: String, password: String) {
        binding.apply {
            viewModel.login(LoginRequest(email, password)).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnSignIn.isEnabled = false
                        }
                        is Result.Success -> {
                            progressBar.visibility = View.GONE
                            btnSignIn.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.sign_in_success,
                                Toast.LENGTH_SHORT
                            ).show()

                            setLogin(result.data.loginResult)
                        }
                        is Result.Error -> {
                            progressBar.visibility = View.GONE
                            btnSignIn.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.sign_in_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }




    private fun clearFocusOnDoneAction(actionId: Int) : Boolean {
        binding.apply {
            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edLoginPassword.clearFocus()
                imm.hideSoftInputFromWindow(edLoginPassword.windowToken, 0)
                return true
            }

            return false
        }
    }

    private fun setLogin(loginResult: LoginResultResponse) {
        loginResult.apply { viewModel.setLogin(UserEntity(userId, name, token)) }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[LoginViewModel::class.java]
    }




}