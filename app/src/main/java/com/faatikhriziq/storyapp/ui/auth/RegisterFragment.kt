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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.faatikhriziq.storyapp.R
import com.faatikhriziq.storyapp.data.remote.request.RegisterRequest
import com.faatikhriziq.storyapp.data.repository.Result
import com.faatikhriziq.storyapp.databinding.FragmentRegisterBinding
import com.faatikhriziq.storyapp.helper.ViewModelFactory


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        setupViewModel()
        setActions()
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[LoginViewModel::class.java]
    }

    private fun playAnimation() {
        val animator = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 0f, 1f)
        animator.duration = 6000
        animator.start()
    }

    private fun clearFocusOnDoneAction(actionId: Int): Boolean {
        binding.apply {
            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edRegisterPassword.clearFocus()
                edRegisterPassword.error = null
                imm.hideSoftInputFromWindow(edRegisterPassword.windowToken, 0)
                return true
            }

            return false
        }
    }

    private fun setActions() {
        binding.apply {
            btnLogin.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
            )

            edRegisterPassword.apply {
                setOnEditorActionListener { _, actionId, _ -> clearFocusOnDoneAction(actionId) }
            }

            btnSignUp.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                register(name, email, password)
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        binding.apply {
            when {
                name.isEmpty() -> {
                    edRegisterName.error = R.string.please_fill_your_name.toString()
                }

                email.isEmpty() -> {
                    edRegisterEmail.error = R.string.please_fill_the_email.toString()
                }

                password.isEmpty() -> {
                    edRegisterPassword.error = R.string.please_fill_the_password.toString()
                }

                password.length < 8 -> {
                    edRegisterPassword.error =
                        R.string.password_must_be_at_least_8_character.toString()
                }

                else -> {
                    executeRegister(name, email, password)
                }
            }
        }
    }

    private fun executeRegister(name: String, email: String, password: String) {
        binding.apply {
            viewModel.register(
                RegisterRequest(name, email, password)
            ).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnSignUp.isEnabled = false
                        }

                        is Result.Success -> {
                            progressBar.visibility = View.GONE
                            btnSignUp.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.create_an_account_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            moveToLoginFragment()
                        }

                        is Error -> {
                            btnSignUp.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.create_an_account_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            btnSignUp.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.create_an_account_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun moveToLoginFragment() {
        val fragmentManager = parentFragmentManager
        fragmentManager.popBackStack()
    }
}