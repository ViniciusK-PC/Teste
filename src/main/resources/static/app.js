const API_URL = '/api/coupons';

// Estado da aplicação
let coupons = [];

// Elementos DOM
const couponGrid = document.getElementById('couponGrid');
const modal = document.getElementById('modal');
const couponForm = document.getElementById('couponForm');
const toast = document.getElementById('toast');

// Inicialização
document.addEventListener('DOMContentLoaded', fetchCoupons);

// Fetch - Listar Cupons
async function fetchCoupons() {
    try {
        const response = await fetch(API_URL);
        const data = await response.json();
        if (data.status === 200) {
            coupons = data.data;
            renderCoupons();
        }
    } catch (error) {
        showToast('Erro ao carregar cupons', 'error');
    }
}

// Renderizar cards na tela
function renderCoupons() {
    couponGrid.innerHTML = coupons.map((c, index) => `
        <div class="coupon-card" style="animation-delay: ${index * 0.1}s">
            <span class="coupon-code">${c.code}</span>
            <div class="coupon-discount">${c.discountValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</div>
            <div class="coupon-date">
                <i data-lucide="calendar" size="16"></i>
                Expira em: ${new Date(c.expirationDate).toLocaleDateString('pt-BR')}
            </div>
            <div class="actions">
                <button class="btn btn-delete" onclick="deleteCoupon(${c.id})">
                    <i data-lucide="trash-2" size="18"></i>
                </button>
            </div>
        </div>
    `).join('');
    // Reinicia os ícones do Lucide
    lucide.createIcons();
}

// Create - Criar Novo Cupom
couponForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(couponForm);
    const payload = {
        code: formData.get('code'),
        discountValue: parseFloat(formData.get('discountValue')),
        expirationDate: formData.get('expirationDate')
    };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showToast('Cupom criado com sucesso!', 'success');
            closeModal();
            fetchCoupons();
            couponForm.reset();
        } else {
            showToast(data.message || 'Erro ao criar cupom', 'error');
        }
    } catch (error) {
        showToast('Erro de conexão', 'error');
    }
});

// Delete - Exclusão Lógica
async function deleteCoupon(id) {
    if (!confirm('Tem certeza que deseja remover este cupom?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (response.ok) {
            showToast('Cupom removido!', 'success');
            fetchCoupons();
        }
    } catch (error) {
        showToast('Erro ao deletar', 'error');
    }
}

// UI Helpers
function openModal() { modal.style.display = 'flex'; }
function closeModal() { modal.style.display = 'none'; }

function showToast(message, type) {
    toast.textContent = message;
    toast.style.borderLeft = `4px solid ${type === 'success' ? '#22c55e' : '#ef4444'}`;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

// Fechar modal ao clicar fora
window.onclick = (e) => { if (e.target === modal) closeModal(); }
