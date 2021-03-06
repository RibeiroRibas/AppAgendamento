package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_REMOVE;
import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_UPDATE;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;
import br.com.beautystyle.util.SortByClientName;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ListClientHolder> {

    private final List<Client> clientList;
    private final List<Client> clientListToFilter;
    private final Context context;
    private AdapterListener.OnClientClickListener onItemClickListener;

    public ClientListAdapter(Context context) {
        this.context = context;
        this.clientList = new ArrayList<>();
        this.clientListToFilter = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.item_job_and_client, parent, false);
        return new ListClientHolder(createdView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListClientHolder holder, int position) {
        if (!clientList.isEmpty()) {
            Client client = clientList.get(position);
            holder.setClientName(client);
        }
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public void setOnItemClickListener(AdapterListener.OnClientClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void filteredClient(String newText) {
        if (newText.isEmpty()) {
            itemRangeInserted();
        } else {
            itemRangeRemoved(newText);
        }
    }

    private void itemRangeRemoved(String newText) {
        List<Client> filteredClientList = clientList.stream()
                .filter(client ->
                        !client.getName().toLowerCase()
                                .contains(newText.toLowerCase())
                )
                .collect(Collectors.toList());
        for (Client client : filteredClientList) {
            int index = clientList.indexOf(client);
            itemRemoved(client, index);
        }
    }

    private void itemRemoved(Client client, int index) {
        clientList.remove(client);
        notifyItemRemoved(index);
    }

    private void itemRangeInserted() {
        notifyItemRangeRemoved(0, this.clientList.size());
        this.clientList.clear();
        this.clientList.addAll(clientListToFilter);
        notifyItemRangeInserted(0, this.clientList.size());
    }

    public void publishResultsInsert(Client client) {
        clientList.add(client);
        clientListToFilter.add(client);
        clientList.sort(new SortByClientName());
        notifyItemInserted(clientList.indexOf(client));
    }

    public void publishClientList(List<Client> clients) {
        notifyItemRangeRemoved(0, clientList.size());
        clientList.clear();
        clientListToFilter.clear();
        clientList.addAll(clients);
        clientList.sort(new SortByClientName());
        clientListToFilter.addAll(clientList);
        notifyItemRangeInserted(0, clientList.size());
    }

    public void publishContactList(List<Client> contactList) {
        clientListToFilter.addAll(contactList);
        clientListToFilter.sort(new SortByClientName());
        int size = clientList.size();
        clientList.clear();
        notifyItemRangeRemoved(0, size);
        clientList.addAll(clientListToFilter);
        notifyItemRangeInserted(0, clientList.size());
    }

    public void publishResultsRemoved(Client client, int position) {
        itemRemoved(client, position);
        clientListToFilter.remove(client);
    }

    public void publishResultsUpdate(Client client, int position) {
        clientList.set(position, client);
        clientListToFilter.set(position, client);
        notifyItemChanged(position, client);
    }

    public Client getClientAtPosition(int position) {
        return clientList.get(position);
    }

    class ListClientHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final TextView nameClient;
        private Client client;

        public ListClientHolder(@NonNull View itemView) {
            super(itemView);
            this.nameClient = itemView.findViewById(R.id.tv_name);

            onClickListener();
            itemView.setOnCreateContextMenuListener(this);
        }

        private void onClickListener() {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(client));
        }

        public void setClientName(Client client) {
            this.client = client;
            nameClient.setText(client.getName());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 0, ITEM_MENU_UPDATE);
            menu.add(this.getAdapterPosition(), 2, 1, ITEM_MENU_REMOVE);
        }
    }
}
