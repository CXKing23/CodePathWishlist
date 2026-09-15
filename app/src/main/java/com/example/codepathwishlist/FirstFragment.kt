package com.example.codepathwishlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.codepathwishlist.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    // Keeping the items in a simple ArrayList for this small app.
    private val wishlist = ArrayList<Wish>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {
            // Read what the user typed into the three input boxes.
            val name = binding.nameInput.text.toString().trim()
            val price = binding.priceInput.text.toString().trim()
            val link = binding.urlInput.text.toString().trim()

            // An item name is the only required field.
            if (name.isEmpty()) {
                binding.nameInput.error = "Enter an item name"
                return@setOnClickListener
            }

            wishlist.add(Wish(name, price, link))

            // Clear the form after the item was added.
            binding.nameInput.text?.clear()
            binding.priceInput.text?.clear()
            binding.urlInput.text?.clear()
            showWishlist()
        }
    }

    private fun showWishlist() {
        // Rebuild the small list every time it changes.
        binding.wishlistContainer.removeAllViews()
        binding.emptyMessage.visibility = if (wishlist.isEmpty()) View.VISIBLE else View.GONE

        for (wish in wishlist) {
            val itemView = layoutInflater.inflate(R.layout.wishlist_item, binding.wishlistContainer, false)
            val title = itemView.findViewById<TextView>(R.id.itemName)
            val details = itemView.findViewById<TextView>(R.id.itemDetails)

            title.text = wish.name
            details.text = if (wish.price.isBlank()) "No price added" else "Price: ${wish.price}"

            itemView.setOnClickListener {
                if (wish.url.isBlank()) {
                    Toast.makeText(context, "No link was added for this item", Toast.LENGTH_SHORT).show()
                } else {
                    // Add https if the user only typed a site name.
                    var url = wish.url
                    if (!url.startsWith("http")) url = "https://$url"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }

            itemView.setOnLongClickListener {
                // Long pressing gives the user a chance to delete an item.
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete item?")
                    .setMessage("Remove ${wish.name} from your wishlist?")
                    .setPositiveButton("Delete") { _, _ ->
                        wishlist.remove(wish)
                        showWishlist()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            binding.wishlistContainer.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// One wishlist item and the information we want to save for it.
data class Wish(val name: String, val price: String, val url: String)
