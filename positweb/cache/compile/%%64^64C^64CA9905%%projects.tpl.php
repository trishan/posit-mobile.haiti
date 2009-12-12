<?php /* Smarty version 2.6.26, created on 2009-12-11 18:31:58
         compiled from projects.tpl */ ?>
<?php $_smarty_tpl_vars = $this->_tpl_vars;
$this->_smarty_include(array('smarty_include_tpl_file' => "header.tpl", 'smarty_include_vars' => array('title' => 'Projects Page','tab' => 'projects')));
$this->_tpl_vars = $_smarty_tpl_vars;
unset($_smarty_tpl_vars);
 ?>
<h2>All Projects</h2>
<p><a href="project.new">New project</a></p>
<div class="project_list">
	<?php if ($this->_tpl_vars['projects']): ?>
	<?php $_from = $this->_tpl_vars['projects']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['project']):
?>
	<div class="list-item">
		<a href="project.display?id=<?php echo $this->_tpl_vars['project']['id']; ?>
"><?php echo $this->_tpl_vars['project']['name']; ?>
</a> 
		<p class="description"><?php echo $this->_tpl_vars['project']['description']; ?>
</p>
	</div>
	<?php endforeach; endif; unset($_from); ?>
	<?php else: ?>
		<em>There are no projects on this server yet.</em>
	<?php endif; ?>
</div>
<?php $_smarty_tpl_vars = $this->_tpl_vars;
$this->_smarty_include(array('smarty_include_tpl_file' => "footer.tpl", 'smarty_include_vars' => array()));
$this->_tpl_vars = $_smarty_tpl_vars;
unset($_smarty_tpl_vars);
 ?>