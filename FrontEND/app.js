const API_URL = 'http://localhost:8080/api/coupons';

let coupons = [];
let editId = null;

const couponGrid = document.getElementById('couponGrid');
const searchInput = document.getElementById('searchInput');
const createModal = document.getElementById('createModal');
const editModal = document.getElementById('editModal');
const toast = document.getElementById('toast');

// Inicializar Ambiente de Teste
document.addEventListener('DOMContentLoaded', fetchCoupons);

async function fetchCoupons() {
    try {
        const response = await fetch(API_URL);
        const data = await response.json();
        coupons = data.data || [];
        renderCoupons(coupons);
    } catch (error) {
        showToast('ERRO: Certifique-se que o Backend (Spring) esta rodando na porta 8080.');
    }
}

async function handleCreate(e) {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    const originalText = btn.textContent;
    
    const payload = {
        code: e.target.code.value,
        discountValue: parseFloat(e.target.discountValue.value),
        expirationDate: e.target.expirationDate.value
    };

    const currentYear = new Date().getFullYear();
    const selectedDate = new Date(payload.expirationDate + 'T00:00:00');
    
    if (selectedDate.getFullYear() !== currentYear) {
        showToast(`BLOQUEADO: Ano ${selectedDate.getFullYear()} não permitido. Use ${currentYear}.`);
        return;
    }

    try {
        btn.disabled = true;
        btn.textContent = 'Processando...';
        
        const res = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if(res.ok) {
            showToast('TESTE SUCESSO: Cupom cadastrado!');
            closeModals();
            fetchCoupons();
            e.target.reset();
        } else {
            const err = await res.json();
            showToast('ERRO NEGOCIO: ' + (err.message || 'Dados invalidos'));
        }
    } catch (e) { 
        showToast('ERRO CONEXÃO: Backend inacessivel'); 
    } finally {
        btn.disabled = false;
        btn.textContent = originalText;
    }
}

async function handleUpdate(e) {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    const originalText = btn.textContent;

    const payload = {
        code: e.target.code.value,
        discountValue: parseFloat(e.target.discountValue.value),
        expirationDate: e.target.expirationDate.value
    };

    const currentYear = new Date().getFullYear();
    const selectedDate = new Date(payload.expirationDate + 'T00:00:00');

    if (selectedDate.getFullYear() !== currentYear) {
        showToast(`BLOQUEADO: Ano ${selectedDate.getFullYear()} não permitido. Use ${currentYear}.`);
        return;
    }

    try {
        btn.disabled = true;
        btn.textContent = 'Atualizando...';

        const res = await fetch(`${API_URL}/${editId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if(res.ok) {
            showToast('TESTE SUCESSO: Cupom atualizado!');
            closeModals();
            fetchCoupons();
        }
    } catch (e) { 
        showToast('ERRO: Falha ao atualizar'); 
    } finally {
        btn.disabled = false;
        btn.textContent = originalText;
    }
}

async function handleDelete(id) {
    if(!confirm('Confirmar Exclusao Logica (Soft Delete)?')) return;
    try {
        const res = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if(res.ok) {
            showToast('TESTE SUCESSO: Soft delete executado!');
            fetchCoupons();
        }
    } catch (e) { showToast('ERRO: Falha ao deletar'); }
}

function renderCoupons(list) {
    if(list.length === 0) {
        couponGrid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 2rem; color: var(--text-muted)">Nenhum cupom ativo encontrado no sistema.</div>';
        return;
    }
    couponGrid.innerHTML = list.map(c => `
        <div class="coupon-card">
            <div style="display:flex; justify-content:space-between; align-items:center">
                <span class="coupon-code">${c.code}</span>
                <span style="color:var(--success); font-weight:bold; font-size:1.1rem">${c.discountValue.toLocaleString('pt-BR', {style:'currency', currency:'BRL'})}</span>
            </div>
            <p style="color:var(--text-muted); font-size:0.8rem; margin-top:0.6rem">Validade: ${new Date(c.expirationDate).toLocaleDateString()}</p>
            <div class="actions">
                <button class="btn btn-outline" style="flex:1" onclick='openEditModal(${JSON.stringify(c)})'>Editar</button>
                <button class="btn btn-danger" onclick="handleDelete(${c.id})">Deletar</button>
            </div>
        </div>
    `).join('');
}

searchInput.addEventListener('input', (e) => {
    const term = e.target.value.toLowerCase();
    const filtered = coupons.filter(c => c.code.toLowerCase().includes(term));
    renderCoupons(filtered);
});

function openEditModal(coupon) {
    editId = coupon.id;
    const form = document.getElementById('editForm');
    form.code.value = coupon.code;
    form.discountValue.value = coupon.discountValue;
    form.expirationDate.value = coupon.expirationDate;
    editModal.style.display = 'flex';
}

function openCreateModal() { createModal.style.display = 'flex'; }
function closeModals() {
    createModal.style.display = 'none';
    editModal.style.display = 'none';
}

function showToast(msg) {
    toast.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

document.getElementById('createForm').onsubmit = handleCreate;
document.getElementById('editForm').onsubmit = handleUpdate;
window.onclick = (e) => { if(e.target.className === 'modal-overlay') closeModals(); }
