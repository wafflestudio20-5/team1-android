package com.waffle22.wafflytime.ui.login


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import com.waffle22.wafflytime.databinding.FragmentMainHomeBinding
import com.waffle22.wafflytime.network.dto.BoardType
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.ui.SettingsActivity
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListAdapter
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListFragmentDirections
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListViewModel
import com.waffle22.wafflytime.ui.mainpage.MainHomeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.net.URL

class MainHomeFragment : Fragment() {
    private lateinit var binding: FragmentMainHomeBinding
    private val viewModel: MainHomeViewModel by sharedViewModel()
    private val viewModel22: BoardListViewModel by sharedViewModel()
    private lateinit var alertDialog: AlertDialog
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().graph.setStartDestination(R.id.mainHomeFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        getResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                settingsActivityReceiver(result)
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 현재 가지고있는 Auth 데이터로 UserInfo Fetch 를 시도한다.



        val basicBoardListAdapter = BoardListAdapter{
            val action = MainHomeFragmentDirections.actionMainHomeFragmentToBoardFragment(it.boardId, BoardType.Common)
            this.findNavController().navigate(action)
        }
        viewModel22.basicBoards.observe(this.viewLifecycleOwner){items->
            items.let{
                basicBoardListAdapter.submitList(it)
            }
        }
        binding.defaultBoards.adapter = basicBoardListAdapter
        binding.defaultBoards.layoutManager = LinearLayoutManager(this.context)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel22.boardLoadingState.collect{
                    showBoardsLogic(it)
                }
            }
        }
        viewModel22.getAllBoards()
// Login Verification
        if (!viewModel.isLogin()) {
            this.findNavController()
                .navigate(MainHomeFragmentDirections.actionGlobalLoginFragment())
        }

        binding.button1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://my.snu.ac.kr/"))
            startActivity(intent)
        }
        binding.button2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bookimpact.com/libseat/search/view.html?num=3359"))
            startActivity(intent)
        }
        binding.button3.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.snu.ac.kr/about/gwanak/shuttles/shuttle_stops"))
            startActivity(intent)
        }
        binding.button4.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.snu.ac.kr/snunow/notice/genernal"))
            startActivity(intent)
        }
        binding.button5.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.snu.ac.kr/academics/resources/calendar"))
            startActivity(intent)
        }
        binding.button6.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lib.snu.ac.kr/"))
            startActivity(intent)
        }
        binding.buttonSearch.setOnClickListener {
            viewModel.logOut()
            this.findNavController().navigate(MainHomeFragmentDirections.actionGlobalLoginFragment())
            activity?.viewModelStore?.clear()
        }

        binding.buttonMyPage.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            getResult.launch(intent)
        }
    }

    fun settingsActivityReceiver(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val doLogout = it.getBooleanExtra("doLogout", false)
                if (doLogout) {
                    findNavController().navigate(MainHomeFragmentDirections.actionGlobalLoginFragment())
                    activity?.viewModelStore?.clear()
                }
            }
        }
    }
    private fun showBoardsLogic(status: LoadingStatus){
        when (status){
            LoadingStatus.Standby -> Toast.makeText(context, "로딩중", Toast.LENGTH_SHORT).show()
            LoadingStatus.Success -> {
                Toast.makeText(context, "로딩 완료", Toast.LENGTH_SHORT).show()
                Log.v("BoardListFragment", "Board Loading Success")
            }
            LoadingStatus.Error -> Toast.makeText(context, viewModel22.errorMessage, Toast.LENGTH_SHORT).show()
            LoadingStatus.Corruption -> Toast.makeText(context, "알 수 없는 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        }
    }
}

