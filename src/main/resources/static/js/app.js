// ===== CONFIGURAÇÃO GLOBAL DA API =====
const API_BASE_URL = 'http://localhost:8080/api';
let currentUser = null;
let coletas = [];
let depoimentos = [];
let sortDirection = 'asc';
let currentPhotoBase64 = "";

// ===== INICIALIZAÇÃO =====
window.onload = function() {
    checkSession();
    loadInitialData();
};

// ===== VERIFICAÇÃO DE SESSÃO =====
function checkSession() {
    const savedToken = localStorage.getItem('app_token');
    const savedUser = localStorage.getItem('logged_user');
    
    if (savedToken && savedUser) {
        currentUser = JSON.parse(savedUser);
        
        // Verificar se aceitou termos
        const termsAccepted = localStorage.getItem('terms_accepted_' + currentUser.id);
        if (!termsAccepted) {
            document.getElementById('terms-blocker').classList.remove('hidden');
            document.getElementById('main-app').classList.add('hidden');
            document.getElementById('auth-screen').classList.add('hidden');
        } else {
            document.getElementById('terms-blocker').classList.add('hidden');
            document.getElementById('auth-screen').classList.add('hidden');
            document.getElementById('main-app').classList.remove('hidden');
            
            updateProfileDisplay();
            adaptInterfaceToRole();
            loadUserColetas();
            loadDepoimentos();
            updateDashboard();
        }
    } else {
        document.getElementById('auth-screen').classList.remove('hidden');
        document.getElementById('main-app').classList.add('hidden');
        document.getElementById('terms-blocker').classList.add('hidden');
    }
}

function loadInitialData() {
    // Carregar dados públicos (coletas, depoimentos, stats)
    loadPublicColetas();
    loadDepoimentos();
}

// ===== AUTENTICAÇÃO - ALTERNAR ABA =====
function toggleAuthTab(tab) {
    if (tab === 'login') {
        document.getElementById('login-form').classList.remove('hidden');
        document.getElementById('register-form').classList.add('hidden');
        document.getElementById('btn-tab-login').className = "flex-1 pb-3 text-xs font-black uppercase tracking-wider text-solid-yellow border-b-2 border-solid-yellow";
        document.getElementById('btn-tab-register').className = "flex-1 pb-3 text-xs font-black uppercase tracking-wider text-gray-500 hover:text-white";
    } else {
        document.getElementById('login-form').classList.add('hidden');
        document.getElementById('register-form').classList.remove('hidden');
        document.getElementById('btn-tab-login').className = "flex-1 pb-3 text-xs font-black uppercase tracking-wider text-gray-500 hover:text-white";
        document.getElementById('btn-tab-register').className = "flex-1 pb-3 text-xs font-black uppercase tracking-wider text-solid-yellow border-b-2 border-solid-yellow";
    }
}

// ===== REGISTRO =====
async function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('reg-name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    const role = document.getElementById('reg-role').value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Cadastro Efetuado', 'Sua conta foi criada. Faça o login para entrar.', '#59ABA7');
            toggleAuthTab('login');
        } else {
            showToast('Erro de Cadastro', data.message || 'Erro ao criar usuário', '#D7425E');
        }
    } catch (e) {
        showToast('Erro de Cadastro', 'Falha ao conectar ao servidor: ' + e.message, '#D7425E');
    }
}

// ===== LOGIN =====
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const role = document.getElementById('login-role').value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok && data.data && data.data.token) {
            // Salvar token e usuário
            localStorage.setItem('app_token', data.data.token);
            
            // Criar objeto user com informações básicas
            const user = {
                id: data.data.userId || data.data.id || 'user_' + Date.now(),
                name: email.split('@')[0].toUpperCase(),
                email: email,
                role: role,
                avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80'
            };
            
            localStorage.setItem('logged_user', JSON.stringify(user));
            currentUser = user;
            
            showToast('Bem-vindo', 'Login efetuado com sucesso!', '#59ABA7');
            checkSession();
        } else {
            showToast('Erro de Login', data.message || 'Email ou senha inválidos', '#D7425E');
        }
    } catch (e) {
        showToast('Erro de Login', 'Falha ao conectar ao servidor: ' + e.message, '#D7425E');
    }
}

// ===== LOGOUT =====
function logout() {
    localStorage.removeItem('app_token');
    localStorage.removeItem('logged_user');
    currentUser = null;
    coletas = [];
    depoimentos = [];
    checkSession();
}

// ===== RECUPERAÇÃO DE SENHA =====
function recoverPassword() {
    showCustomAlert('Recuperação de Senha', 'Como estamos em ambiente de testes, um link fictício foi enviado. Utilize as credenciais padrão para simular o login.');
}

// ===== ACEITAR TERMOS =====
function acceptTermsAndProceed() {
    const acceptedCheckbox = document.getElementById('accept-checkbox');
    if (!acceptedCheckbox.checked) {
        showToast('Atenção', 'É obrigatório aceitar os termos de uso para continuar.', '#D7425E');
        return;
    }
    localStorage.setItem('terms_accepted_' + currentUser.id, 'true');
    checkSession();
    showToast('Termos Aceitos', 'Obrigado por apoiar a economia circular.', '#59ABA7');
}

// ===== INTERFACE - ADAPTAR AO PAPEL =====
function adaptInterfaceToRole() {
    const role = currentUser.role;
    document.getElementById('header-user-role').innerText = role === 'ADMIN' ? 'Coordenador Operacional' : 'Destinação Circular';
    
    if (role === 'ADMIN') {
        document.getElementById('nav-admin-only').classList.remove('hidden');
        document.getElementById('nav-cliente-only').classList.add('hidden');
        document.querySelectorAll('.admin-only').forEach(el => el.classList.remove('hidden'));
        document.querySelectorAll('.cliente-only').forEach(el => el.classList.add('hidden'));
        switchTab('dashboard');
    } else {
        document.getElementById('nav-admin-only').classList.add('hidden');
        document.getElementById('nav-cliente-only').classList.remove('hidden');
        document.querySelectorAll('.admin-only').forEach(el => el.classList.add('hidden'));
        document.querySelectorAll('.cliente-only').forEach(el => el.classList.remove('hidden'));
        switchTab('dashboard');
    }
}

// ===== SIMULAR MUDANÇA DE PAPEL =====
function toggleMockRole() {
    if (currentUser) {
        currentUser.role = currentUser.role === 'ADMIN' ? 'CLIENTE' : 'ADMIN';
        localStorage.setItem('logged_user', JSON.stringify(currentUser));
        showToast('Perfil Alterado', `Simulando visão de ${currentUser.role}`, '#48A8E2');
        adaptInterfaceToRole();
        updateProfileDisplay();
        updateDashboard();
    }
}

// ===== PERFIL DO USUÁRIO =====
function updateProfileDisplay() {
    if (currentUser) {
        document.getElementById('user-display-name').innerText = currentUser.name;
        document.getElementById('user-display-role').innerText = currentUser.role === 'ADMIN' ? 'Coordenador' : 'Gerador';
        document.getElementById('user-avatar').src = currentUser.avatar;
    }
}

function openProfileModal() {
    if (currentUser) {
        document.getElementById('prof-name').value = currentUser.name;
        document.getElementById('prof-email').value = currentUser.email;
        document.getElementById('prof-avatar').value = currentUser.avatar;
        document.getElementById('profile-modal').classList.remove('hidden');
    }
}

function closeProfileModal() {
    document.getElementById('profile-modal').classList.add('hidden');
}

async function saveUserProfile(e) {
    e.preventDefault();
    currentUser.name = document.getElementById('prof-name').value;
    currentUser.email = document.getElementById('prof-email').value;
    currentUser.avatar = document.getElementById('prof-avatar').value;
    
    localStorage.setItem('logged_user', JSON.stringify(currentUser));
    showToast('Perfil Atualizado', 'Suas alterações foram guardadas localmente.', '#59ABA7');
    closeProfileModal();
    updateProfileDisplay();
}

// ===== ALTERNÂNCIA DE ABAS =====
function switchTab(tabId) {
    document.getElementById('tab-dashboard').classList.add('hidden');
    document.getElementById('tab-nova-solicitacao').classList.add('hidden');
    document.getElementById('tab-gerenciar-coletas').classList.add('hidden');
    document.getElementById('tab-frota').classList.add('hidden');
    document.getElementById('tab-mural-depoimentos').classList.add('hidden');

    document.querySelectorAll('.tab-link').forEach(link => {
        link.classList.remove('active', 'bg-[#1A1D26]', 'border-l-4', 'border-solid-yellow', 'font-bold', 'text-white');
        link.classList.add('text-gray-400', 'hover:text-white');
    });

    document.getElementById(`tab-${tabId}`).classList.remove('hidden');
}

// ===== UPLOAD DE IMAGENS =====
function handleFileSelected(e) {
    const file = e.target.files[0];
    if (file) {
        document.getElementById('file-name-indicator').innerText = file.name;
        const reader = new FileReader();
        reader.onload = function(evt) {
            currentPhotoBase64 = evt.target.result;
            document.getElementById('image-preview').src = currentPhotoBase64;
            document.getElementById('image-preview-container').classList.remove('hidden');
        };
        reader.readAsDataURL(file);
    }
}

// ===== COLETAS - CARREGAR DADOS PÚBLICOS =====
async function loadPublicColetas() {
    try {
        const response = await fetch(`${API_BASE_URL}/coletas`);
        if (response.ok) {
            coletas = await response.json();
            updateDashboard();
        }
    } catch (e) {
        console.error('Erro ao carregar coletas públicas:', e);
    }
}

// ===== COLETAS - CARREGAR COLETAS DO USUÁRIO =====
async function loadUserColetas() {
    const token = localStorage.getItem('app_token');
    if (!token) return;

    try {
        const response = await fetch(`${API_BASE_URL}/coletas/user`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            coletas = await response.json();
            filterAndRenderTable();
            renderAdminColetas();
        }
    } catch (e) {
        console.error('Erro ao carregar coletas do usuário:', e);
    }
}

// ===== COLETAS - CRIAR NOVA SOLICITAÇÃO =====
async function handleNewSolicitacao(e) {
    e.preventDefault();
    const token = localStorage.getItem('app_token');
    if (!token) {
        showToast('Erro', 'Você não está autenticado', '#D7425E');
        return;
    }

    const origem = document.getElementById('sol-origem').value;
    const contato = document.getElementById('sol-contato').value;
    const material = document.getElementById('sol-material').value;
    const peso = parseInt(document.getElementById('sol-peso').value);
    const local = document.getElementById('sol-regiao').value;

    if (!currentPhotoBase64) {
        showToast('Impedimento técnico', 'A foto para triagem inicial do material é obrigatória.', '#D7425E');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/coletas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                material,
                peso,
                local,
                origem,
                contato,
                fotoBase64: currentPhotoBase64
            })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Solicitação Efetuada', `Coleta criada com sucesso!`, '#59ABA7');
            
            // Limpar formulário
            document.getElementById('sol-origem').value = "";
            document.getElementById('sol-contato').value = "";
            document.getElementById('sol-peso').value = "";
            document.getElementById('file-name-indicator').innerText = "Resíduos limpos e secos apenas";
            document.getElementById('image-preview-container').classList.add('hidden');
            currentPhotoBase64 = "";

            // Recarregar dados
            loadUserColetas();
            loadPublicColetas();
            switchTab('dashboard');
        } else {
            showToast('Erro na Solicitação', data.message || 'Erro ao criar coleta', '#D7425E');
        }
    } catch (e) {
        showToast('Erro na Solicitação', 'Falha ao conectar: ' + e.message, '#D7425E');
    }
}

// ===== TABELA - FILTRO E BUSCA =====
function filterAndRenderTable() {
    const searchTerm = document.getElementById('search-term').value.toLowerCase();
    const selectedMaterial = document.getElementById('filter-material').value;
    const selectedStatus = document.getElementById('filter-status').value;
    const tbody = document.getElementById('user-coletas-table');
    tbody.innerHTML = '';

    let filtered = coletas;
    
    // Se for cliente, mostrar apenas suas coletas
    if (currentUser && currentUser.role === 'CLIENTE') {
        // Filtro seria por userId, mas como não temos certeza do campo na resposta
        // vamos mostrar tudo por enquanto
    }

    // Filtro de termo de busca
    filtered = filtered.filter(c => 
        (c.id ? c.id.toString().toLowerCase().includes(searchTerm) : false) ||
        (c.origem ? c.origem.toLowerCase().includes(searchTerm) : false) ||
        (c.local ? c.local.toLowerCase().includes(searchTerm) : false)
    );

    // Filtro por tipo de material
    if (selectedMaterial !== 'TODOS') {
        filtered = filtered.filter(c => c.material === selectedMaterial);
    }

    // Filtro por status
    if (selectedStatus !== 'TODOS') {
        filtered = filtered.filter(c => c.status === selectedStatus);
    }

    // Renderizar linhas
    let sumWeight = 0;
    filtered.forEach(c => {
        sumWeight += c.peso || 0;
        let statusClass = 'text-solid-red border-solid-red bg-[#D7425E]/10';
        if (c.status === 'Em Trânsito') statusClass = 'text-solid-blue border-solid-blue bg-[#48A8E2]/10';
        if (c.status === 'Finalizada') statusClass = 'text-solid-green border-solid-green bg-[#59ABA7]/10';

        const tr = document.createElement('tr');
        tr.className = 'hover:bg-[#151720] transition-colors border-b border-[#1E2235]';
        tr.innerHTML = `
            <td class="py-4 px-4 font-mono font-bold text-white">${c.id || 'N/A'}</td>
            <td class="py-4 px-4">${c.origem || 'N/A'} <span class="text-[10px] text-gray-500 block">${c.local || 'N/A'}</span></td>
            <td class="py-4 px-4 font-bold">${c.material || 'N/A'}</td>
            <td class="py-4 px-4 font-mono font-bold">${c.peso || 0} kg</td>
            <td class="py-4 px-4">
                ${c.fotoBase64 ? `<img src="${c.fotoBase64}" class="w-10 h-10 object-cover border border-[#2D3142]" alt="Foto">` : '<span class="text-gray-500">Sem foto</span>'}
            </td>
            <td class="py-4 px-4">
                <span class="px-2 py-0.5 border text-[10px] font-bold ${statusClass}">${c.status || 'Pendente'}</span>
            </td>
            <td class="py-4 px-4 text-right">
                <button onclick="shareOS('${c.id}')" class="text-solid-blue hover:underline text-[10px]" title="Compartilhar"><i class="fa-solid fa-share-nodes"></i> Compartilhar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Atualizar totais
    document.getElementById('footer-count').innerText = `${filtered.length} solicitações`;
    document.getElementById('footer-weight-sum').innerText = `${sumWeight.toLocaleString('pt-BR')} kg`;
}

// ===== TABELA - ORDENAÇÃO =====
function toggleSortOrder(column) {
    sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
    coletas.sort((a, b) => {
        const aVal = a[column] || 0;
        const bVal = b[column] || 0;
        if (sortDirection === 'asc') {
            return aVal - bVal;
        } else {
            return bVal - aVal;
        }
    });
    filterAndRenderTable();
    showToast('Ordenado', `Dados ordenados por ${column} em ordem ${sortDirection}`, '#F2B422');
}

// ===== ADMIN - RENDERIZAR COLETAS =====
async function renderAdminColetas() {
    const token = localStorage.getItem('app_token');
    if (!token) return;

    try {
        const response = await fetch(`${API_BASE_URL}/coletas`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const allColetas = await response.json();
            const tbody = document.getElementById('admin-coletas-table');
            tbody.innerHTML = '';

            allColetas.forEach(c => {
                let statusClass = 'text-solid-red border-solid-red bg-[#D7425E]/10';
                if (c.status === 'Em Trânsito') statusClass = 'text-solid-blue border-solid-blue bg-[#48A8E2]/10';
                if (c.status === 'Finalizada') statusClass = 'text-solid-green border-solid-green bg-[#59ABA7]/10';

                const tr = document.createElement('tr');
                tr.className = 'hover:bg-[#151720] border-b border-[#1E2235]';
                tr.innerHTML = `
                    <td class="py-4 px-4 font-mono text-white">${c.id || 'N/A'}</td>
                    <td class="py-4 px-4">${c.origem || 'N/A'}</td>
                    <td class="py-4 px-4 font-bold">${c.material || 'N/A'}</td>
                    <td class="py-4 px-4 font-mono font-bold text-solid-yellow">${c.peso || 0} kg</td>
                    <td class="py-4 px-4">
                        ${c.fotoBase64 ? `<img src="${c.fotoBase64}" class="w-10 h-10 object-cover border border-[#2D3142]" alt="Foto">` : '<span class="text-gray-500">Sem</span>'}
                    </td>
                    <td class="py-4 px-4"><span class="px-2 py-0.5 border text-[10px] font-bold ${statusClass}">${c.status || 'Pendente'}</span></td>
                    <td class="py-4 px-4 text-right space-x-2">
                        ${c.status === 'Pendente' ? `
                            <button onclick="advanceAdminStatus(${c.id}, 'Em Trânsito')" class="bg-solid-blue text-white text-[10px] px-2 py-1 font-bold">DESPACHAR</button>
                        ` : ''}
                        ${c.status === 'Em Trânsito' ? `
                            <button onclick="advanceAdminStatus(${c.id}, 'Finalizada')" class="bg-solid-green text-white text-[10px] px-2 py-1 font-bold">CONFIRMAR</button>
                        ` : ''}
                        ${c.status === 'Finalizada' ? `
                            <span class="text-gray-500 font-mono text-[10px]">Concluído</span>
                        ` : ''}
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        console.error('Erro ao renderizar coletas admin:', e);
    }
}

// ===== ADMIN - AVANÇAR STATUS =====
async function advanceAdminStatus(id, newStatus) {
    const token = localStorage.getItem('app_token');
    if (!token) return;

    try {
        const response = await fetch(`${API_BASE_URL}/coletas/${id}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ status: newStatus })
        });

        if (response.ok) {
            showToast('Status Modificado', `Carga atualizada para ${newStatus}`, '#59ABA7');
            renderAdminColetas();
            loadPublicColetas();
            updateDashboard();
        }
    } catch (e) {
        showToast('Erro', 'Falha ao atualizar status: ' + e.message, '#D7425E');
    }
}

// ===== DASHBOARD - ATUALIZAR =====
async function updateDashboard() {
    try {
        const response = await fetch(`${API_BASE_URL}/coletas/stats`);
        if (response.ok) {
            const stats = await response.json();
            document.getElementById('dash-app-total').innerText = `${(stats.totalWeight || 0).toLocaleString('pt-BR')} kg`;
            document.getElementById('dash-app-actives').innerText = stats.activeColetas || 0;
            
            // Calcular CO2 do usuário (apenas coletas do usuário)
            let userTotal = 0;
            coletas.forEach(c => {
                if (c.userId === (currentUser?.id || -1) || currentUser?.role === 'CLIENTE') {
                    userTotal += c.peso || 0;
                }
            });
            document.getElementById('dash-user-total').innerText = `${userTotal.toLocaleString('pt-BR')} kg`;
            const userCO2 = ((userTotal * 2.7) / 1000).toFixed(2);
            document.getElementById('dash-user-co2').innerText = `${userCO2} Ton`;
        }
    } catch (e) {
        console.error('Erro ao atualizar dashboard:', e);
    }
}

// ===== DEPOIMENTOS - CARREGAR =====
async function loadDepoimentos() {
    try {
        const response = await fetch(`${API_BASE_URL}/depoimentos`);
        if (response.ok) {
            depoimentos = await response.json();
            renderDepoimentos();
        }
    } catch (e) {
        console.error('Erro ao carregar depoimentos:', e);
    }
}

// ===== DEPOIMENTOS - RENDERIZAR =====
function renderDepoimentos() {
    const container = document.getElementById('mural-container');
    if (!container) return;
    
    container.innerHTML = '';

    depoimentos.forEach(d => {
        const card = document.createElement('div');
        card.className = 'bg-solid-card p-5 border-l-4 border-solid-yellow space-y-2';
        card.innerHTML = `
            <div class="flex justify-between items-start">
                <h4 class="text-xs font-mono text-solid-yellow uppercase tracking-widest">${d.titulo || 'Depoimento'}</h4>
                <span class="text-[10px] text-gray-500">${new Date(d.createdAt || Date.now()).toLocaleDateString('pt-BR')}</span>
            </div>
            <p class="text-xs text-gray-300 leading-relaxed">${d.mensagem || ''}</p>
            <div class="text-[10px] text-gray-500 text-right">Por: <strong class="text-white">Usuário</strong></div>
        `;
        container.appendChild(card);
    });
}

// ===== DEPOIMENTOS - CRIAR NOVO =====
async function handleNewDepoimento(e) {
    e.preventDefault();
    const token = localStorage.getItem('app_token');
    if (!token) {
        showToast('Erro', 'Você não está autenticado', '#D7425E');
        return;
    }

    const titulo = document.getElementById('dep-title').value;
    const mensagem = document.getElementById('dep-msg').value;

    try {
        const response = await fetch(`${API_BASE_URL}/depoimentos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ titulo, mensagem })
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Mural Atualizado', 'Seu depoimento foi postado!', '#59ABA7');
            document.getElementById('dep-title').value = "";
            document.getElementById('dep-msg').value = "";
            loadDepoimentos();
        } else {
            showToast('Erro', data.message || 'Erro ao criar depoimento', '#D7425E');
        }
    } catch (e) {
        showToast('Erro', 'Falha ao conectar: ' + e.message, '#D7425E');
    }
}

// ===== COMPARTILHAMENTO =====
function shareOS(id) {
    const coleta = coletas.find(c => c.id == id);
    if (coleta) {
        showCustomAlert('Compartilhar OS', `Carga: ${coleta.id}\nMaterial: ${coleta.material}\nPeso: ${coleta.peso}kg\nOrigem: ${coleta.origem}\n\nCódigo de rastreamento copiado.`);
    }
}

// ===== EXPORTAÇÃO =====
function exportData(format) {
    let dataStr = "";
    let filename = "relatorio_coletas_solidarium";
    
    if (format === 'csv') {
        dataStr = "ID,Origem,Material,Peso(kg),Local,Status\n";
        coletas.forEach(c => {
            dataStr += `${c.id},"${c.origem}","${c.material}",${c.peso},"${c.local}","${c.status}"\n`;
        });
        downloadBlob(new Blob([dataStr], {type: 'text/csv;charset=utf-8;'}), filename + ".csv");
        showToast('Exportação concluída', 'Planilha exportada com sucesso.', '#59ABA7');
    } else if (format === 'json') {
        dataStr = JSON.stringify(coletas, null, 2);
        downloadBlob(new Blob([dataStr], {type: 'application/json'}), filename + ".json");
        showToast('Intercâmbio JSON', 'Estrutura de dados exportada para Web Services.', '#48A8E2');
    }
}

function downloadBlob(blob, filename) {
    const link = document.createElement("a");
    if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

// ===== SIMULADOR MOBILE =====
function openSimulatorModal() {
    document.getElementById('simulator-modal').classList.remove('hidden');
}

function closeSimulatorModal() {
    document.getElementById('simulator-modal').classList.add('hidden');
}

function simulatePermission(recurso) {
    showToast('Permissão Mobile', `Acesso à ${recurso} simulado com sucesso.`, '#48A8E2');
}

// ===== MENU MOBILE =====
function toggleMobileMenu() {
    const menu = document.getElementById('mobile-menu');
    menu.classList.toggle('hidden');
}

// ===== ALERTAS E NOTIFICAÇÕES =====
function showCustomAlert(title, message) {
    document.getElementById('custom-alert-title').innerText = title;
    document.getElementById('custom-alert-message').innerText = message;
    document.getElementById('custom-alert-modal').classList.remove('hidden');
}

function closeCustomAlert() {
    document.getElementById('custom-alert-modal').classList.add('hidden');
}

function showToast(title, message, color) {
    const toast = document.getElementById('toast');
    document.getElementById('toast-title').innerText = title;
    document.getElementById('toast-message').innerText = message;
    toast.style.borderLeftColor = color;
    toast.classList.remove('hidden');
    setTimeout(() => {
        toast.classList.add('hidden');
    }, 3500);
}
